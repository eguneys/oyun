package oyun.api

import play.api.mvc.RequestHeader

import oyun.user.{ HeaderUserContext, UserContext }

case class PageData()


object PageData {

  def anon(req: RequestHeader) = PageData()
  
}

sealed trait Context extends oyun.user.UserContextWrapper {

  val userContext: UserContext
  val pageData: PageData

  def lang = userContext.lang

}


sealed abstract class BaseContext(
  val userContext: oyun.user.UserContext,
  val pageData: PageData
) extends Context


final class HeaderContext(
  headerContext: HeaderUserContext,
  data: PageData
) extends BaseContext(headerContext, data)

object Context {

  def apply(userContext: HeaderUserContext, pageData: PageData): HeaderContext =
    new HeaderContext(userContext, pageData)
  
}
