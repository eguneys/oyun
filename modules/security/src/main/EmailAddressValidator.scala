package oyun.security

import oyun.common.EmailAddress
import oyun.user.{ User, UserRepo }

final class EmailAddressValidator() {

  private def isAcceptable(email: EmailAddress): Boolean =
    true

  def validate(email: EmailAddress): Option[EmailAddressValidator.Acceptable] =
    isAcceptable(email) option EmailAddressValidator.Acceptable(email)

}


object EmailAddressValidator {

  case class Acceptable(acceptable: EmailAddress)

}
