package oyun.user

import akka.actor._
import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration
import play.api.libs.ws.WSClient

import oyun.common.config._
import oyun.common.LightUser

private class UserConfig(
  @ConfigName("collection.user") val collectionUser: CollName,
  @ConfigName("password.bpass.secret") val passwordBPassSecret: Secret
)

@Module
final class Env(
  appConfig: Configuration,
  db: oyun.db.Db,
  cacheApi: oyun.memo.CacheApi
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem, ws: WSClient) {

  private val config = appConfig.get[UserConfig]("user")(AutoConfig.loader)

  val repo = new UserRepo(db(config.collectionUser))

  val lightUserApi: LightUserApi = wire[LightUserApi]
  val lightUser = lightUserApi.async

  lazy val jsonView = wire[JsonView]

  private lazy val passHasher = new PasswordHasher(
    secret = config.passwordBPassSecret,
    logRounds = 10
  )

  lazy val authenticator = wire[Authenticator]
  
}
