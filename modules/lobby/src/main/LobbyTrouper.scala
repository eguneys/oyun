package oyun.lobby

import org.joda.time.DateTime
import scala.concurrent.duration._
import scala.concurrent.Promise

import actorApi._
import oyun.common.{ AtMost, Every }
import oyun.hub.Trouper
import oyun.socket.Socket.{ Sri }
import oyun.game.Masa
import oyun.user.User

final private class LobbyTrouper(
  biter: Biter,
  masaRepo: oyun.game.MasaRepo
)(implicit ec: scala.concurrent.ExecutionContext) extends Trouper {

  import LobbyTrouper._

  private var socket: Trouper = Trouper.stub

  val process: Trouper.Receive = {
    // solve circular reference
    case SetSocket(trouper) => socket = trouper

    case BiteMasa(masaId, sri, user) =>
      biteMasa(masaId, sri, user)
    case msg @ JoinMasa(sri, masa, joinSide) =>
      socket ! msg
    case Tick(promise) =>

  }

  private def biteMasa(masaId: Masa.ID, sri: Sri, user: Option[LobbyUser]) =
    masaRepo byId masaId foreach { _.foreach { masa => 
      biter(masa, sri, user) foreach this.!
    } }



}

private object LobbyTrouper {

  case class SetSocket(trouper: Trouper)

  private case class Tick(promise: Promise[Unit])

  def start(
    broomPeriod: FiniteDuration
  )(
    makeTrouper: () => LobbyTrouper
  )(implicit ec: scala.concurrent.ExecutionContext, system: akka.actor.ActorSystem) = {

    val trouper = makeTrouper()
    oyun.common.Bus.subscribe(trouper, "lobbyTrouper")
    oyun.common.ResilientScheduler(
      every = Every(broomPeriod),
      atMost = AtMost(10 seconds),
      initialDelay = 7 seconds
    ) { trouper.ask[Unit](Tick) }
    trouper
  }

}
