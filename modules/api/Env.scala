package oyun.api

// import akka.actor._
import com.softwaremill.macwire._
// import play.api.{ Configuration }

// import oyun.common.config._

final class Env(
  // appConfig: Configuration,
  //net: NetConfig,
  masaEnv: oyun.masa.Env,
  // gameEnv: oyun.game.Env
)(implicit ec: scala.concurrent.ExecutionContext) {

  lazy val lobbyApi = wire[LobbyApi]

  lazy val masaApi = wire[MasaApi]

}
