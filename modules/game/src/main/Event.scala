package oyun.game

import play.api.libs.json._

import Player._

sealed trait Event {
  def typ: String
  def data: JsValue
  def only: Option[Side] = None
  def owner: Boolean = false
}

case class SitoutNext(side: Side, oPlayer: Option[Player]) extends Event {

  def typ = "sitoutnext"

  def data = Json.obj(
    "side" -> side,
    "player" -> (oPlayer.fold[JsValue](JsNull){ player => Json.obj(
      "status" -> player.status
    ) })
  )

}

case class BuyIn(side: Side, player: Player) extends Event {

  def typ = "buyin"

  def data = Json.obj(
    "side" -> side,
    "player" -> Json.obj(
      "id" -> player.id,
      "user"  -> player.userId,
      "avatar" -> player.avatar,
      "status" -> player.status
    )
  )

}

case class Me(side: Side, player: Player) extends Event {
  def typ = "me"

  def data = Json.obj(
    "side" -> side,
    "status" -> player.status.forsyth
  )

  override def only = Some(side)
}
