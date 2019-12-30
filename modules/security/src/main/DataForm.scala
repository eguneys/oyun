package oyun.security

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints

import oyun.common.{ EmailAddress }

final class DataForm(
)(implicit ec: scala.concurrent.ExecutionContext) {

  import DataForm._

  private val anyEmail = trimField(text).verifying(Constraints.emailAddress)

  private def trimField(m: Mapping[String]) = m.transform[String](_.trim, identity)

  object signup {

    private val username = trimField(nonEmptyText)
      .verifying(
        Constraints minLength 2,
        Constraints maxLength 20
      )

    val website = Form(
      mapping(
        "username" -> trimField(username),
        "password" -> text(minLength = 4),
        "email" -> anyEmail,
        "fp" -> optional(nonEmptyText),
        "g-recaptcha-response" -> optional(nonEmptyText)
      )(SignupData.apply)(_ => None)
    )

  }
}

object DataForm {

  case class SignupData(
    username: String,
    password: String,
    email: String,
    fp: Option[String],
    `g-recaptcha-response`: Option[String]) {

    def recaptchaResponse = `g-recaptcha-response`

    def realEmail = EmailAddress(email)

  }
  
}
