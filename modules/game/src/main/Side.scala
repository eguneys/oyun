package oyun.game

import play.api.libs.json._

import poker.{ Side }

object SideJson {

  implicit val sideWrites = new Writes[Side] {
    def writes(side: Side) = JsString(side.index.toString)
  }

}
