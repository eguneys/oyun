package oyun.lobby

import play.api.libs.json._
import scala.concurrent.Promise


import actorApi._
import oyun.masa.Pov
import oyun.hub.Trouper
import oyun.socket.RemoteSocket.{ Protocol => P, _ }
import oyun.socket.Socket.{ Sri }
import oyun.socket.Socket.{ makeMessage, Sri }
import oyun.user.User

final class LobbySocket(
  userRepo: oyun.user.UserRepo,
  remoteSocketApi: oyun.socket.RemoteSocket,
  lobby: LobbyTrouper
)(implicit ec: scala.concurrent.ExecutionContext) {

  import LobbySocket._
  type SocketController = PartialFunction[(String, JsObject), Unit]

  val trouper: Trouper = new Trouper {
    private val members = scala.collection.mutable.AnyRefMap.empty[SriStr, Member]

    val process: Trouper.Receive = {
      case GetMember(sri, promise) => promise success members.get(sri.value)

      case Join(member) => 
        println(members.size)
        members += (member.sri.value -> member)
      case LeaveBatch(sris) => sris foreach quit
      case LeaveAll =>
        members.clear()

      case JoinMasa(sri, masa, joinSide) =>
        send(P.Out.tellSri(sri, joinMasaRedirect(masa pov joinSide)))
    }

    private def joinMasaRedirect(pov: Pov) =
      makeMessage("redirect",
        Json.obj(
          "id" -> pov.fullId,
          "url" -> s"/${pov.fullId}"
        )
          .add("cookie" -> oyun.masa.AnonCookie.json(pov))
      )

    private def quit(sri: Sri): Unit = {
      members -= sri.value
    }
  }

  // solve circular reference
  lobby ! LobbyTrouper.SetSocket(trouper)


  def controller(member: Member): SocketController = {
    case ("hookIn", _) =>
    case ("join", o) =>
      o str "d" foreach { id =>
        lobby ! BiteMasa(id, member.sri, member.user)
      }
  }

  private def getOrConnect(sri: Sri, userOpt: Option[User.ID]): Fu[Member] =
    {
      trouper.ask[Option[Member]](GetMember(sri, _)) getOrElse {
        userOpt ?? userRepo.enabledById flatMap { user =>
          (user ?? { u =>
            // remoteSocketApi.baseHandler(P.In.ConnectUser(u.id))
            funit
          }) inject {
            val member = Member(sri, user map { LobbyUser.make(_) })
            trouper ! Join(member)
            member
          }
        }
      }
    }

  private val handler: Handler = {
    case P.In.ConnectSris(cons) =>
    case P.In.DisconnectSris(sris) => 
      trouper ! LeaveBatch(sris)
    case P.In.WsBoot =>
      logger.warn("Remote socket boot")
      trouper ! LeaveAll
    case P.In.TellSri(sri, user, tpe, msg) if messagesHandled(tpe) =>
      getOrConnect(sri, user) foreach { member =>
        controller(member).applyOrElse(tpe -> msg, {
          case _ => logger.warn(s"Can't handle $tpe")
        }: SocketController)
      }
  }

  private val messagesHandled: Set[String] = Set("join", "hookIn", "hookOut")




  remoteSocketApi.subscribe("lobby-in", P.In.baseReader)(handler orElse remoteSocketApi.baseHandler)

  private val send: String => Unit = remoteSocketApi.makeSender("lobby-out").apply _
  
}

private object LobbySocket {

  type SriStr = String

  case class Member(sri: Sri, user: Option[LobbyUser]) {
    def userId = user.map(_.id)
    def isAuth = userId.isDefined
  }

  case class Join(member: Member)
  case class GetMember(sri: Sri, promise: Promise[Option[Member]])

}
