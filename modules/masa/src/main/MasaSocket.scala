package oyun.masa

import akka.actor.{ ActorSystem, Cancellable, Scheduler }
import play.api.libs.json._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import actorApi._
import actorApi.masa._
import poker.{ Side }
import poker.format.Uci
import oyun.game.{ Masa, Event }
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
      terminationDelay schedule Masa.Id(id)
      duct
    },
    initialCapacity = 32768
  )

  def tellMasa(masaId: Masa.Id, msg: Any): Unit = masas.tell(masaId.value, msg)

  private lazy val masaHandler: Handler = {
    case Protocol.In.PlayerOnlines(onlines) =>
      onlines foreach {
        case (masaId, Some(on)) =>
          tellMasa(masaId, on)
          terminationDelay cancel masaId
        case (masaId, None) =>
          if (masas exists masaId.value)
            terminationDelay schedule masaId
      }
    case Protocol.In.PlayerMove(fullId, uci) =>
      tellMasa(fullId.masaId, HumanPlay(fullId.userId, uci))
    case Protocol.In.Sit(id, side) =>
      tellMasa(id.masaId, Buyin(id.userId, side))
    case Protocol.In.SitoutNext(masaId, side, value) =>
      tellMasa(masaId, SitoutNext(side, value))
    case P.In.WsBoot =>
      logger.warn("Remote socket boot")
      masas.tellAll(MasaDuct.WsBoot)
  }

  private def finishMasa(masaId: Masa.Id): Unit =
    masas.terminate(masaId.value, _ ! MasaDuct.Stop)

  private lazy val send: String => Unit = remoteSocketApi.makeSender("m-out").apply _

  remoteSocketApi.subscribe("m-in", Protocol.In.reader)(
    masaHandler orElse remoteSocketApi.baseHandler
  )// >>- send(P.Out.boot)


  system.scheduler.scheduleWithFixedDelay(60 seconds, 60 seconds) { () =>
    oyun.mon.masa.ductCount.update(masas.size)
  }

  private val terminationDelay = new TerminationDelay(system.scheduler, 1 minute, finishMasa)

}

object MasaSocket {

  object Protocol {

    object In {
      case class PlayerOnlines(onlines: Iterable[(Masa.Id, Option[RoomCrowd])]) extends P.In
      case class PlayerDo(fullId: FullId, tpe: String) extends P.In
      case class PlayerMove(fullId: FullId, uci: Uci) extends P.In
      case class Sit(fullId: FullId, side: Side) extends P.In
      case class SitoutNext(masaId: Masa.Id, side: Side, value: Boolean) extends P.In

      val reader: P.In.Reader = raw => {
        raw.path match {
          case "r/ons" =>
            PlayerOnlines {
              P.In.commas(raw.args) map {
                _ splitAt Masa.masaIdSize match {
                  case (masaId, cs) =>
                    (Masa.Id(masaId),
                      if (cs.isEmpty) None 
                      else
                        Some(RoomCrowd(cs.toInt))
                    )
                }
              }
            }.some
          case "m/move" =>
            raw.get(2) {
              case Array(fullId, uciS) =>
                Uci.Move(uciS) map { uci =>
                  PlayerMove(FullId(fullId), uci)
                }
            }
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

      def resyncPlayer(fullId: FullId) = s"m/resync/players $fullId"

      def masaPlayerStore(masaId: Masa.Id, value: String) =
        s"m/players $masaId $value"

      def tellVersion(roomId: RoomId, version: SocketVersion, e: Event) = {
        val flags = new StringBuilder(2)

        val only = e.only.map(_.index) getOrElse "-"

        s"m/ver $roomId $version $flags ${only} ${e.typ} ${e.data}"
      }

    }


  }

  final private class TerminationDelay(
    scheduler: Scheduler,
    duration: FiniteDuration,
    terminate: Masa.Id => Unit
  )(implicit ec: scala.concurrent.ExecutionContext) {

    import java.util.concurrent.ConcurrentHashMap

    private[this] val terminations = new ConcurrentHashMap[String, Cancellable](128)

    def schedule(masaId: Masa.Id): Unit = terminations.compute(
      masaId.value,
      (id, canc) => {
        Option(canc).foreach(_.cancel)
        scheduler.scheduleOnce(duration) {
          terminations remove id
          terminate(Masa.Id(id))
        }
      }
    )

    def cancel(masaId: Masa.Id): Unit =
      Option(terminations remove masaId.value).foreach(_.cancel)

  }

}
