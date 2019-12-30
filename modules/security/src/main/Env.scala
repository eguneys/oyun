package oyun.security

import akka.actor._
import com.softwaremill.macwire._
import play.api.Configuration
import play.api.libs.ws.WSClient

import oyun.common.config._
import oyun.user.{ Authenticator, UserRepo }

final class Env(
  appConfig: Configuration,
  ws: WSClient,
  net: NetConfig,
  userRepo: UserRepo,
  authenticator: Authenticator,
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {


  private val config = appConfig.get[SecurityConfig]("security")(SecurityConfig.loader)

  val recaptchaPublicConfig = config.recaptcha.public


  lazy val forms = wire[DataForm]


  lazy val store = new Store()

  lazy val api = wire[SecurityApi]

  lazy val emailAddressValidator = wire[EmailAddressValidator]
  
}

