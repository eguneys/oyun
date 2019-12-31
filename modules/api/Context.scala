package oyun.api

import play.api.mvc.RequestHeader

import oyun.user.{ BodyUserContext, HeaderUserContext, UserContext }

case class PageData()


object PageData {

  def anon(req: RequestHeader) = PageData()
  
}

sealed trait Context extends oyun.user.UserContextWrapper {

  val userContext: UserContext
  val pageData: PageData

  def lang = userContext.lang

  lazy val currentBg = "dark"

}


sealed abstract class BaseContext(
  val userContext: oyun.user.UserContext,
  val pageData: PageData
) extends Context


final class BodyContext[A](
  val bodyContext: BodyUserContext[A],
  data: PageData
) extends BaseContext(bodyContext, data) {

  def body = bodyContext.body

}

final class HeaderContext(
  headerContext: HeaderUserContext,
  data: PageData
) extends BaseContext(headerContext, data)

object Context {

  def apply(userContext: HeaderUserContext, pageData: PageData): HeaderContext =
    new HeaderContext(userContext, pageData)

  def apply[A](userContext: BodyUserContext[A], pageData: PageData): BodyContext[A] =
    new BodyContext(userContext, pageData)
  
}
