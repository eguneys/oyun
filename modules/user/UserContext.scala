package oyun.user

import play.api.mvc.{ Request, RequestHeader }

sealed trait UserContext {

  val req: RequestHeader

  val me: Option[User]

}

sealed abstract class BaseUserContext(
  val req: RequestHeader,
  val me: Option[User]) extends UserContext {

}

final class HeaderUserContext(r: RequestHeader, m: Option[User]) extends BaseUserContext(r, m)

trait UserContextWrapper extends UserContext {
  val userContext: UserContext
  val req = userContext.req
  val me = userContext.me
  
}


object UserContext {

  def apply(
    req: RequestHeader,
    me: Option[User]): HeaderUserContext = new HeaderUserContext(req, me)

}
