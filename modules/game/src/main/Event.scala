package oyun.game

import play.api.libs.json._

import Player._

import poker.{ Side, Situation }
import SideJson._

sealed trait Event {
  def typ: String
  def data: JsValue
  def only: Option[Side] = None
  def owner: Boolean = false
}

object Event {

  case class Deal(fen: String, seatIndexes: List[Side]) extends Event {
    def typ = "deal"

    def data = Json.obj(
      "fen" -> fen,
      "seatIndexes" -> seatIndexes
    )
  }

  object Deal {
    def apply(
      situation: Situation, seatIndexes: List[Side]): Deal = Deal(
        fen = situation.dealer.visual,
        seatIndexes = seatIndexes
    )
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
}
