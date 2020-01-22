package oyun.game

import play.api.libs.json._

import poker.{ Side, Chips }

object SideJson {

  implicit val sideWrites = new Writes[Side] {
    def writes(side: Side) = JsNumber(side.index)
  }

  implicit val chipWrites = new Writes[Chips] {
    def writes(chips: Chips) = JsString(chips.toString)
  }

}
