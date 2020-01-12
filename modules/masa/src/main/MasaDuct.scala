package oyun.masa

import actorApi._, masa._
import oyun.hub.Duct
import oyun.game.{ Masa, Side }
import oyun.room.RoomSocket.{ Protocol => RP, _ }
import oyun.socket.RemoteSocket.{ Protocol => _, _ }
import oyun.socket.Socket.{ makeMessage, SocketVersion }

final private[masa] class MasaDuct(
  dependencies: MasaDuct.Dependencies,
  masaId: Masa.ID,
  socketSend: String => Unit
)(implicit ec: scala.concurrent.ExecutionContext, proxy: MasaProxy) extends Duct {

  import MasaSocket.Protocol
  import MasaDuct._
  import dependencies._

  private var version = SocketVersion(0)

  def getMasa: Fu[Option[Masa]] = proxy.masa

  val process: Duct.ReceiveAsync = {

    case GetSocketStatus(promise) =>
      fuccess {
        promise success SocketStatus(
          version = version
        )
      }
    case Buyin(userId, side) =>
      handle { masa =>
        sitter.buyin(masa, userId, side)
      }

    case WsBoot =>
      proxy.withMasa { m =>
        val uids = m.seats.map { _ ?? { _.userId } } mkString ","
        socketSend(Protocol.Out.masaPlayerStore(Masa.Id(masaId), uids))
        funit
      }

  }
  
  private def handle(op: Masa => Fu[Events]): Funit =
    proxy withMasa { m =>
      handleAndPublish(op(m))
    }

  private def handleAndPublish(events: Fu[Events]): Funit =
    events dmap publish recover errorHandler("handle")

  private def publish[A](events: Events): Unit =
    if (events.nonEmpty) {
      events.map { e =>
        version = version.inc
        socketSend {
          Protocol.Out.tellVersion(roomId, version, e)
        }
      }
    }

  private def errorHandler(name: String): PartialFunction[Throwable, Unit] = {
    case e: ClientError =>
      logger.info(s"Masa client error $name: ${e.getMessage}")
    case e: Exception =>
      logger.warn(s"$name: ${e.getMessage}")
  }


  def roomId = RoomId(masaId)
}

object MasaDuct {

  case object WsBoot

  private[masa] class Dependencies(
    val masaRepo: oyun.game.MasaRepo,
    val sitter: Sitter
  )

}
