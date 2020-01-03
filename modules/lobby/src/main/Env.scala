package oyun.lobby

import com.softwaremill.macwire._
import play.api.Configuration

import oyun.common.config._

@Module
final class Env(
  appConfig: Configuration,
  db: oyun.db.Db,
  userRepo: oyun.user.UserRepo,
  remoteSocketApi: oyun.socket.RemoteSocket
)(implicit ec: scala.concurrent.ExecutionContext,
  system: akka.actor.ActorSystem) {

  wire[LobbySocket]
  
}
