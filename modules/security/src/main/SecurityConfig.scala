package oyun.security

import com.softwaremill.macwire._
import io.methvin.play.autoconfig._

import oyun.common.config._

final private class SecurityConfig(
  val recaptcha: Recaptcha.Config
)

private object SecurityConfig {

  implicit val loader = AutoConfig.loader[SecurityConfig]
  
}
