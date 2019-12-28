package controllers

import play.api.mvc._

import oyun.api.Context
import oyun.app._
import oyun.common.{ HTTPRequest }

import views._

final class Auth(
  env: Env
) extends OyunController(env) {

  private def api = env.security.api
  private def forms = env.security.forms

  def login = Open { implicit ctx =>
    val referrer = get("referrer")
    referrer.filterNot(_ contains "/login") ifTrue ctx.isAuth match {
      case Some(url) => Redirect(url).fuccess
      case None => Ok(html.auth.login(api.loginForm, referrer)).fuccess
    }
  }

  def authenticate = OpenBody { implicit ctx =>
    def redirectTo(url: String) = if (HTTPRequest isXhr ctx.req) Ok(s"ok:$url") else Redirect(url)
    redirectTo("/").fuccess
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
