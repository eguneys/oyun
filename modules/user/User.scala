package oyun.user

case class User(
  id: String,
  username: String,
  lang: Option[String]
) {


}

object User {

  type ID = String

  type CredentialCheck = ClearPassword => Boolean
  case class LoginCandidate(user: User, check: CredentialCheck) {
    import LoginCandidate._
    def apply(p: ClearPassword): Result = {
      val res = 
        if (check(p)) Success(user)
        else InvalidUsernameOrPassword
      res
    }
    def option(p: ClearPassword): Option[User] = apply(p).toOption
  }
  object LoginCandidate {
    sealed abstract class Result(val toOption: Option[User]) {
      def success = toOption.isDefined
    }

    case class Success(user: User) extends Result(user.some)
    case object InvalidUsernameOrPassword extends Result(none)
  }

  case class ClearPassword(value: String) extends AnyVal {
    override def toString = "ClearPassword(****)"
  }

  val newUsernameRegex = """(?i)[a-z][\w-]{0,28}[a-z0-9]""".r

  def couldBeUsername(str: User.ID) = newUsernameRegex.matches(str)

  def normalize(username: String) = username.toLowerCase

  object BSONFields {

    val id = "_id"
    val username = "username"
    val email = "email"

  }

}
