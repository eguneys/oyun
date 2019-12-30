package oyun.security

import com.softwaremill.macwire._
import io.methvin.play.autoconfig._

import oyun.common.config._

import SecurityConfig._

@Module
final private class SecurityConfig(
  val collection: Collection,
  val recaptcha: Recaptcha.Config
)

private object SecurityConfig {

  case class Collection(
    security: CollName
  )

  implicit val collectionLoader = AutoConfig.loader[Collection]

  implicit val loader = AutoConfig.loader[SecurityConfig]
  
}
