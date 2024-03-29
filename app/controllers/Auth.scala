package controllers

import play.api.libs.json._
import play.api.mvc._

import oyun.api.Context
import oyun.app._
import oyun.common.{ EmailAddress, HTTPRequest }
import oyun.security.{ EmailAddressValidator }
import oyun.user.{ User => UserModel, PasswordHasher }
import UserModel.ClearPassword
import views._

final class Auth(
  env: Env
) extends OyunController(env) {

  private def api = env.security.api
  private def forms = env.security.forms


  private def mobileUserOk(u: UserModel, sessionId: String): Fu[Result] =
    funit map { _ =>
      Ok {
        env.user.jsonView(u) ++ Json.obj(
          "sessionId" -> sessionId
        )
      }
    }

  def authenticateUser(u: UserModel, result: Option[String => Result] = None)
  (implicit ctx: Context): Fu[Result] = api.saveAuthentication(u.id) flatMap { sessionId =>
    negotiate(
      html = fuccess {
        val redirectTo = get("referrer") getOrElse routes.Lobby.home.url
        result.fold(Redirect(redirectTo))(_(redirectTo))
      },
      api = _ => mobileUserOk(u, sessionId)
    ) map authenticateCookie(sessionId)
  }

  def login = Open { implicit ctx =>
    val referrer = get("referrer")
    referrer.filterNot(_ contains "/login") ifTrue ctx.isAuth match {
      case Some(url) => Redirect(url).fuccess
      case None => Ok(html.auth.login(api.loginForm, referrer)).fuccess
    }
  }

  private def authenticateCookie(sessionId: String)(result: Result)(implicit req: RequestHeader) =
    result.withCookies(
      env.oyunCookie.withSession {
        _ + (api.sessionIdKey -> sessionId)
      }
    )

  def authenticate = OpenBody { implicit ctx =>
    def redirectTo(url: String) = if (HTTPRequest isXhr ctx.req) Ok(s"ok:$url") else Redirect(url)

    implicit val req = ctx.body
    val referrer = get("referrer")
    api.usernameOrEmailForm.bindFromRequest.fold(
      err => negotiate(
        html = Unauthorized(html.auth.login(api.loginForm, referrer)).fuccess,
        api = _ => Unauthorized(errorsAsJson(err)).fuccess
      ),
      usernameOrEmail =>
      api.loadLoginForm(usernameOrEmail) flatMap {
        loginForm =>
        loginForm.bindFromRequest.fold(
          err => negotiate(
            html = {
              fuccess(Unauthorized(html.auth.login(err, referrer)))
            },
            api = _ => Unauthorized(errorsAsJson(err)).fuccess
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

  def logout = Open { implicit ctx =>
    val currentSessionId = ~env.security.api.reqSessionId(ctx.req)
    env.security.store.delete(currentSessionId) >>
    negotiate(
      html = Redirect(routes.Auth.login).fuccess,
      api = _ => Ok(Json.obj("ok" -> true)).fuccess
    ).dmap(_.withCookies(env.oyunCookie.newSession))
  }
    

  def signup = Open { implicit ctx =>
    Ok(html.auth.signup(forms.signup.website, env.security.recaptchaPublicConfig)).fuccess
  }
 

  def signupPost = OpenBody { implicit ctx =>
    implicit val req = ctx.body

    negotiate(
      html = forms.signup.website.bindFromRequest.fold(
        err => {
          BadRequest(html.auth.signup(err, env.security.recaptchaPublicConfig)).fuccess
        },
        data =>
        env.security.recaptcha.verify(~data.recaptchaResponse, req).flatMap {
          case false =>
            BadRequest(
              html.auth.signup(forms.signup.website fill data, env.security.recaptchaPublicConfig)
            ).fuccess
          case true =>
            {
              val email = env.security.emailAddressValidator
                .validate(data.realEmail) err s"Invalid email ${data.email}"
              val passwordHash = env.user.authenticator passEnc ClearPassword(data.password)
              env.user.repo
                .create(
                  data.username,
                  passwordHash,
                  email.acceptable
                )
                .orFail(s"No user could be created for ${data.username}")
                .map(_ -> email)
                .flatMap {
                  case (user, EmailAddressValidator.Acceptable(email)) =>
                    welcome(user, email) >> redirectNewUser(user)
                }
            }
        }

      ),
      api = apiVersion =>
      forms.signup.mobile.bindFromRequest.fold(
        err => {
          jsonFormError(err)
        },
        data => {
          val email = env.security.emailAddressValidator
            .validate(data.realEmail) err s"Invalid email ${data.email}"
          val passwordHash = env.user.authenticator passEnc ClearPassword(data.password)
          env.user.repo
            .create(
              data.username,
              passwordHash,
              email.acceptable
            )
            .orFail(s"No user could be created for ${data.username}")
            .map(_ -> email)
            .flatMap {
              case (user, EmailAddressValidator.Acceptable(email)) =>
                welcome(user, email) >> authenticateUser(user)
            }
        }
      )
    )
  }


  private def welcome(user: UserModel, email: EmailAddress)(implicit ctx: Context): Funit = {
    funit
  }

  private def redirectNewUser(user: UserModel)(implicit ctx: Context) = {
    api.saveAuthentication(user.id) flatMap { sessionId =>
      negotiate(
        html = Redirect(routes.User.show(user.username)).fuccess,
        api = _ => ???
      ) map authenticateCookie(sessionId)
    }
  }
 
}
