package oyun.common

import play.api.http.HeaderNames
import play.api.mvc.RequestHeader
import play.api.routing.Router

object HTTPRequest {

  def isXhr(req: RequestHeader): Boolean =
    req.headers get "X-Requested-With" contains "XMLHttpRequest"


    private val ApiVersionHeaderPattern = """application/vnd\.oyunkeyf\.v(\d++)\+json""".r

  def apiVersion(req: RequestHeader): Option[ApiVersion] = {
    req.headers.get(HeaderNames.ACCEPT) flatMap {
      case ApiVersionHeaderPattern(v) => v.toIntOption map ApiVersion.apply
      case _                          => none
    }
  }

  
}
