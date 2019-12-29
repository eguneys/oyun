package controllers

import play.api.mvc._

import oyun.api.Context
import oyun.app._
import oyun.common.{ HTTPRequest }
import oyun.user.{ User => UserModel }

import views._

final class Auth(
  env: Env
) extends OyunController(env) {

  private def api = env.security.api
  private def forms = env.security.forms


  def authenticateUser(u: UserModel, result: Option[String => Result] = None)
  (implicit ctx: Context): Fu[Result] = fuccess(Ok("kadjf"))

  def login = Open { implicit ctx =>
    val referrer = get("referrer")
    referrer.filterNot(_ contains "/login") ifTrue ctx.isAuth match {
      case Some(url) => Redirect(url).fuccess
      case None => Ok(html.auth.login(api.loginForm, referrer)).fuccess
    }
  }

  def authenticate = OpenBody { implicit ctx =>
    def redirectTo(url: String) = if (HTTPRequest isXhr ctx.req) Ok(s"ok:$url") else Redirect(url)

    implicit val req = ctx.body
    val referrer = get("referrer")
    api.usernameOrEmailForm.bindFromRequest.fold(
      err => negotiate(
        html = Unauthorized(html.auth.login(api.loginForm, referrer)).fuccess,
        api = _ => ??? // Unauthorized(errorsAsJson(err)).fuccess
      ),
      usernameOrEmail =>
      api.loadLoginForm(usernameOrEmail) flatMap {
        loginForm =>
        loginForm.bindFromRequest.fold(
          err => negotiate(
            html = fuccess(Unauthorized(html.auth.login(err, referrer))),
            api = _ => ???
          ),
          result =>
          result.toOption match {
            case None => InternalServerError("Authentication error").fuccess
            case Some(u) =>
              authenticateUser(u, Some(redirectTo))
          }
        )
      }
    )
  }

  def signup = Open { implicit ctx =>
    Ok(html.auth.signup(forms.signup.website, env.security.recaptchaPublicConfig)).fuccess
  }
 

  def signupPost = OpenBody { implicit ctx =>
    implicit val req = ctx.body

    negotiate(
      html = ???,
      api = apiVersion =>
      ???
    )
  }
 
}
