package oyun.masa

import actorApi._, masa._
import oyun.hub.Duct
import oyun.game.{ Side }
import oyun.socket.RemoteSocket.{ Protocol => RP, _ }

final private[masa] class MasaDuct(
  dependencies: MasaDuct.Dependencies,
  masaId: Masa.ID,
  socketSend: String => Unit
)(implicit ec: scala.concurrent.ExecutionContext, proxy: MasaProxy) extends Duct {

  import MasaDuct._
  import dependencies._

  def getMasa: Fu[Option[Masa]] = proxy.masa

  val process: Duct.ReceiveAsync = {

    case Sit(userId, side) =>
      handle { masa =>
        sitter.sit(masa, userId, side)
      }

    case WsBoot =>
      funit

  }
  
  private def handle(op: Masa => Fu[Events]): Funit =
    proxy withMasa { m =>
      handleAndPublish(op(m))
    }

  private def handleAndPublish(events: Fu[Events]): Funit =
    events dmap publish recover errorHandler("handle")

  private def publish[A](events: Events): Unit = ()

  private def errorHandler(name: String): PartialFunction[Throwable, Unit] = {
    case e: ClientError =>
      logger.info(s"Masa client error $name: ${e.getMessage}")
    case e: Exception =>
      logger.warn(s"$name: ${e.getMessage}")
  }


}

object MasaDuct {

  case object WsBoot

  private[masa] class Dependencies(
    val masaRepo: MasaRepo,
    val sitter: Sitter
  )

}
