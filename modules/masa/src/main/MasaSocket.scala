package oyun.masa

import akka.actor.{ ActorSystem }
import play.api.libs.json._
import scala.concurrent.ExecutionContext

import actorApi._
import actorApi.masa._
import oyun.game.{ Masa, Side, Event }
import oyun.game.Masa.{ FullId }
import oyun.room.RoomSocket.{ Protocol => RP, _ }
import oyun.socket.RemoteSocket.{ Protocol => P, _ }
import oyun.socket.Socket.SocketVersion
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
      tellMasa(id.masaId, Buyin(id.userId, side))
    case Protocol.In.SitoutNext(masaId, side, value) =>
      tellMasa(masaId, SitoutNext(side, value))
    case P.In.WsBoot =>
      logger.warn("Remote socket boot")
      masas.tellAll(MasaDuct.WsBoot)
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
      case class SitoutNext(masaId: Masa.Id, side: Side, value: Boolean) extends P.In

      val reader: P.In.Reader = raw => {
        raw.path match {
          case "m/sit" =>
            raw.get(2) {
              case Array(fullId, side) =>
                Side(side) map {
                  Sit(FullId(fullId), _)
                }
            }
          case "m/sitoutnext" =>
            raw.get(3) {
              case Array(masaId, side, valueS) =>
                Side(side) map {
                  SitoutNext(Masa.Id(masaId), _, P.In.boolean(valueS))
                }
            }
          case _ => RP.In.reader(raw)
        }
      }
    }

    object Out {

      def masaPlayerStore(masaId: Masa.Id, value: String) =
        s"m/players $masaId $value"

      def tellVersion(roomId: RoomId, version: SocketVersion, e: Event) = {
        val flags = new StringBuilder(2)

        val only = e.only.map(_.index) getOrElse "-"

        s"m/ver $roomId $version $flags ${only} ${e.typ} ${e.data}"
      }

    }


  }

}
