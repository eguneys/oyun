package oyun.hub
package actorApi

import play.api.libs.json._

package socket {

  object remote {
    case class TellSriIn(sri: String, user: Option[String], msg: JsObject)
    case class TellSriOut(sri: String, payload: JsValue)
  }
  
}
