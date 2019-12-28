package oyun.app
package templating

import oyun.user.UserContext
import ui.ScalatagsTemplate._

trait StringHelper {

  def urlencode(str: String) = java.net.URLEncoder.encode(str, "US-ASCII")
  
}
