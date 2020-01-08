package oyun.lobby

import com.softwaremill.macwire._
import play.api.Configuration
import scala.concurrent.duration._

import oyun.common.config._

@Module
final class Env(
  appConfig: Configuration,
  db: oyun.db.Db,
  userRepo: oyun.user.UserRepo,
  masaRepo: oyun.game.MasaRepo,
  remoteSocketApi: oyun.socket.RemoteSocket
)(implicit ec: scala.concurrent.ExecutionContext,
  system: akka.actor.ActorSystem) {

  private lazy val lobbyTrouper = LobbyTrouper.start(
    broomPeriod = 2 seconds
  ) { () =>
    wire[LobbyTrouper]
  }

  private lazy val biter = wire[Biter]

  wire[LobbySocket]
  
}
