package oyun.common

import ornicar.scalalib.Random
import play.api.mvc._

final class OyunCookie(baker: SessionCookieBaker) {


  private val domainRegex = """\.[^.]++\.[^.]++$""".r

  private def domain(req: RequestHeader): String =
    domainRegex.findFirstIn(req.domain).getOrElse(req.domain)


  def withSession(op: Session => Session)(implicit req: RequestHeader): Cookie = cookie(
    baker.COOKIE_NAME,
    baker.encode(baker.serialize(op(req.session)))
  )
  
  def cookie(name: String, 
    value: String,
    maxAge: Option[Int] = None,
    httpOnly: Option[Boolean] = None
  )(implicit req: RequestHeader): Cookie = 
    Cookie(
      name,
      value,
      maxAge orElse 86400.some,
      "/",
      domain(req).some,
      baker.secure || false,
      httpOnly | baker.httpOnly
    )

}

object OyunCookie {

  val sessionId = "sid"

}
