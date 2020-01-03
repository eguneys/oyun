package oyun.api

import akka.actor._
import com.softwaremill.macwire._
import play.api.{ Configuration, Mode }

import oyun.common.config._

final class Env(
  appConfig: Configuration,
  net: NetConfig,
  roundApi: oyun.round.RoundApi
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {

  lazy val lobbyApi = wire[LobbyApi]


}
