package oyun.lobby

import play.api.libs.json._
import scala.concurrent.Promise


import actorApi._
import oyun.hub.Trouper
import oyun.socket.RemoteSocket.{ Protocol => P, _ }
import oyun.socket.Socket.{ Sri }
import oyun.user.User

final class LobbySocket(
  userRepo: oyun.user.UserRepo,
  remoteSocketApi: oyun.socket.RemoteSocket
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

    }

    private def quit(sri: Sri): Unit = {
      members -= sri.value
    }
  }


  def controller(member: Member): SocketController = {
    case ("hookIn", _) =>

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
    case P.In.DisconnectSris(sris) => trouper ! LeaveBatch(sris)
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

  private val messagesHandled: Set[String] = Set("hookIn", "hookOut")




  remoteSocketApi.subscribe("lobby-in", P.In.baseReader)(handler orElse remoteSocketApi.baseHandler)
  
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
