package oyun.security

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.{ Constraint, Valid => FormValid, Invalid, ValidationError }
import play.api.mvc.RequestHeader

import oyun.common.{ EmailAddress }
import oyun.user.{ User, UserRepo }
import User.LoginCandidate

final class SecurityApi(
  userRepo: UserRepo,
  store: Store,
  authenticator: oyun.user.Authenticator,
  emailValidator: EmailAddressValidator,
)(implicit ec: scala.concurrent.ExecutionContext, system: akka.actor.ActorSystem) {


  lazy val usernameOrEmailForm = Form(
    single(
      "username" -> nonEmptyText
    )
  )


  lazy val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  private def loadedLoginForm(candidate: Option[LoginCandidate]) =
    Form(
      mapping(
        "username" -> nonEmptyText,
        "password" -> nonEmptyText
      )(authenticateCandidate(candidate)) {
        case LoginCandidate.Success(user) => (user.username, "", none).some
        case _ => none
      }.verifying(Constraint { (t: LoginCandidate.Result) =>
        t match {
          case LoginCandidate.Success(_) => FormValid
          case LoginCandidate.InvalidUsernameOrPassword =>
            Invalid(Seq(ValidationError("invalidUsernameOrPassword")))
          case err => Invalid(Seq(ValidationError(err.toString)))
        }
      })
    )

  def loadLoginForm(str: String): Fu[Form[LoginCandidate.Result]] = {
    emailValidator.validate(EmailAddress(str)) match {
      case Some(EmailAddressValidator.Acceptable(email)) =>
        authenticator.loginCandidateByEmail(email.normalize)
      case None if User.couldBeUsername(str) => 
        authenticator.loginCandidateById(User normalize str)
      case _ => fuccess(none)
    }
  } map loadedLoginForm _
   


  def restoreUser(req: RequestHeader): Fu[Option[User]] =
    reqSessionId(req) ?? { sessionId =>
      store userId sessionId flatMap {
        _ ?? { d =>
          userRepo byId d
        }
      }
    }

  val sessionIdKey = "sessionId"

  def reqSessionId(req: RequestHeader): Option[String] =
    req.session.get(sessionIdKey) orElse req.headers.get(sessionIdKey)

}
