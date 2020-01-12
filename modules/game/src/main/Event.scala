package oyun.game

import play.api.libs.json._

sealed trait Event {
  def typ: String
  def data: JsValue
  def only: Option[Side] = None
  def owner: Boolean = false
}

case class BuyIn(side: Side, player: Player) extends Event {

  def typ = "buyin"

  def data = Json.obj(
    "side" -> side,
    "player" -> Json.obj(
      "id" -> player.id,
      "user"  -> player.userId,
      "status" -> player.status.forsyth
    )
  )

}

case class Me(side: Side, player: Player) extends Event {
  def typ = "me"

  def data = Json.obj(
    "status" -> player.status.forsyth
  )

  override def only = Some(side)
}
