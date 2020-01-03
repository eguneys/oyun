package oyun.round

import akka.actor._
import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration

import oyun.common.config._
import oyun.user.User

private class RoundConfig(
)


@Module
final class Env(
  appConfig: Configuration,
  db: oyun.db.Db,
  lightUserApi: oyun.user.LightUserApi
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {


  private val config = appConfig.get[RoundConfig]("round")(AutoConfig.loader)

  lazy val RoundRepo = new RoundRepo()
  lazy val PlayerRepo = new PlayerRepo()

  lazy val api: RoundApi = wire[RoundApi]


  lazy val apiJsonView = wire[ApiJsonView]

}
