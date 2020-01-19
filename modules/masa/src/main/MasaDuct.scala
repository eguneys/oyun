package oyun.masa

import actorApi._, masa._
import oyun.hub.Duct
import poker.{ Side }
import oyun.game.{ Pov, Masa }
import oyun.user.User
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

    // round

    case MaybeDeal => handle { masa =>
      masa.dealable ?? dealer.deal(masa)
    }

    case p: HumanPlay =>
      handle(p.userId) { pov =>
        player.human(p, this)(pov)
      }.addEffects(
        err => {
          socketSend(Protocol.Out.resyncPlayer(Masa.Id(masaId) full p.userId))
        },
        lap => {
        }
      )

    case Buyin(userId, side) =>
      handle { masa =>
        sitter.buyin(masa, userId, side) >>-
        publishMasaPlayerStore >>- {
          this ! MaybeDeal
        }
      }.addEffects(
        err => {
          socketSend(Protocol.Out.resyncPlayer(Masa.Id(masaId) full userId))
        },
        lap => {
        }
      )
    case SitoutNext(side, value) =>
      handle { masa =>
        sitter.sitoutNext(masa, side, value) >>-
        publishMasaPlayerStore >>- {
          this ! MaybeDeal
        }
      }

    case WsBoot =>
      proxy.withMasa { m =>
        funit >>-
        publishMasaPlayerStore
      }
    case Stop =>
  }

  private def publishMasaPlayerStore: Funit = proxy withMasa { m =>
    val uids = m.seats.map { _ ?? { _.userId } } mkString ","
    socketSend(Protocol.Out.masaPlayerStore(Masa.Id(masaId), uids))
    funit
  }
  
  private def handle(op: Masa => Fu[Events]): Funit =
    proxy withMasa { m =>
      handleAndPublish(op(m))
    }

  private def handle(userId: User.ID)(op: Pov => Fu[Events]): Funit =
    proxy.withPov(userId) {
      _ ?? { pov =>
        handleAndPublish(op(pov))
      }
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

  case object Stop
  case object WsBoot

  private[masa] class Dependencies(
    val masaRepo: oyun.game.MasaRepo,
    val sitter: Sitter,
    val dealer: Dealer,
    val player: Player
  )

}
