package oyun.app
package templating

import oyun.user.UserContext
import ui.ScalatagsTemplate._

trait StringHelper {

  def urlencode(str: String) = java.net.URLEncoder.encode(str, "US-ASCII")


  implicit def oyunRichString(str: String): OyunRichString = new OyunRichString(str)
}

final class OyunRichString(val str: String) extends AnyVal {

  def active(other: String, one: String = "active") = if (str == other) one else ""
  def activeO(other: String, one: String = "active") = if (str == other) Some(one) else None
  
}
