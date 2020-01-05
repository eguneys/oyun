package oyun.masa

import akka.actor._
import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration

import oyun.common.config._
import oyun.user.User

private class MasaConfig(
)


@Module
final class Env(
  appConfig: Configuration,
  db: oyun.db.Db,
  lightUserApi: oyun.user.LightUserApi
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {


  private val config = appConfig.get[MasaConfig]("masa")(AutoConfig.loader)

  lazy val RoundRepo = new RoundRepo()
  lazy val MasaRepo = new MasaRepo()

  private lazy val masaScheduler = wire[MasaScheduler]
  masaScheduler.scheduleNow()

  lazy val api: MasaApi = wire[MasaApi]

  lazy val jsonView = wire[JsonView]

  lazy val apiJsonView = wire[ApiJsonView]

}
