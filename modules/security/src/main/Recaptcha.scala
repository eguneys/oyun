package oyun.security

import play.api.mvc.RequestHeader
import io.methvin.play.autoconfig._

import oyun.common.HTTPRequest
import oyun.common.config._

trait Recaptcha {

  def verify(response: String, req: RequestHeader): Fu[Boolean]

}

private object Recaptcha {

  case class Config (
    endpoint: String,
    @ConfigName("public_key") publicKey: String,
    @ConfigName("private_key") privateKey: Secret,
    enabled: Boolean
  ) {

    def public = RecaptchaPublicConfig(publicKey, enabled)

  }

  implicit val configLoader = AutoConfig.loader[Config]
  
}
