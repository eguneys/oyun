package oyun.masa

import akka.actor.{ ActorSystem }
import play.api.libs.json._
import scala.concurrent.ExecutionContext

import actorApi._
import actorApi.masa._
import oyun.game.{ Side }
import oyun.masa.Masa.{ FullId }
import oyun.socket.RemoteSocket.{ Protocol => P, _ }
import oyun.hub.DuctConcMap

final class MasaSocket(
  remoteSocketApi: oyun.socket.RemoteSocket,
  masaDependencies: MasaDuct.Dependencies,
  proxyDependencies: MasaProxy.Dependencies
)(implicit ec: ExecutionContext, system: ActorSystem) {

  import MasaSocket._

  def getMasa(masaId: Masa.ID): Fu[Option[Masa]] = masas.getOrMake(masaId).getMasa


  val masas = new DuctConcMap[MasaDuct](
    mkDuct = id => {
      val proxy = new MasaProxy(id, proxyDependencies)
      val duct = new MasaDuct(
        dependencies = masaDependencies,
        masaId = id,
        socketSend = send
      )(ec, proxy)
      duct
    },
    initialCapacity = 32768
  )

  def tellMasa(masaId: Masa.Id, msg: Any): Unit = masas.tell(masaId.value, msg)

  private lazy val masaHandler: Handler = {
    case Protocol.In.Sit(id, side) =>
      tellMasa(id.masaId, Sit(id.userId, side))
  }

  private lazy val send: String => Unit = remoteSocketApi.makeSender("m-out").apply _

  remoteSocketApi.subscribe("m-in", Protocol.In.reader)(
    masaHandler orElse remoteSocketApi.baseHandler
  )// >>- send(P.Out.boot)

}

object MasaSocket {

  object Protocol {

    object In {
      case class PlayerDo(fullId: FullId, tpe: String) extends P.In
      case class Sit(fullId: FullId, side: Side) extends P.In

      val reader: P.In.Reader = raw => {
        raw.path match {
          case "m/sit" =>
            raw.get(2) {
              case Array(fullId, side) =>
                Side(side) map {
                  Sit(FullId(fullId), _)
                }
            }
        }
      }
    }

    object Out {

    }


  }

}
