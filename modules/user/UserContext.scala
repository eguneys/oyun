package oyun.user

import play.api.mvc.{ Request, RequestHeader }
import play.api.i18n.Lang

sealed trait UserContext {

  val req: RequestHeader

  val me: Option[User]

  def lang: Lang

  def isAuth = me.isDefined

  def isAnon = !isAuth

  def is(user: User): Boolean = me contains user

  def userId = me.map(_.id)

  def username = me.map(_.username)

  def usernameOrAnon = username | "Anonymous"

}

sealed abstract class BaseUserContext(
  val req: RequestHeader,
  val me: Option[User],
  val lang: Lang) extends UserContext {

}

final class HeaderUserContext(r: RequestHeader, m: Option[User], l: Lang) extends BaseUserContext(r, m, l)

trait UserContextWrapper extends UserContext {
  val userContext: UserContext
  val req = userContext.req
  val me = userContext.me

}


object UserContext {

  def apply(
    req: RequestHeader,
    me: Option[User],
    lang: Lang): HeaderUserContext = new HeaderUserContext(req, me, lang)

}
