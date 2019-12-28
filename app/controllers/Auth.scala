package controllers

import oyun.api.Context
import oyun.app._

import views._

final class Auth(
  env: Env
) extends OyunController(env) {

  private def api = env.security.api

  def login = Open { implicit ctx =>
    val referrer = get("referrer")
    referrer.filterNot(_ contains "/login") ifTrue ctx.isAuth match {
      case Some(url) => Redirect(url).fuccess
      case None => Ok(html.auth.login(api.loginForm, referrer)).fuccess
    }
  }
  
}
