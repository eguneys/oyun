package oyun.game

import play.api.libs.json._

import Player._

import poker.{ PlayerAct,
  Side,
  Situation,
  Move => PokerMove }
import SideJson._

sealed trait Event {
  def typ: String
  def data: JsValue
  def only: Option[Side] = None
  def owner: Boolean = false
}

object Event {

  case class Move(
    act: PlayerAct
  ) extends Event {
    def typ = "move"

    def data = Json.obj(
    )
    


  }

  object Move {

    def apply(
      move: PokerMove,
      situation: Situation): Move = Move(
      move.playerAct
    )

  }


  case class Deal(fen: String, seatIndexes: Vector[Side]) extends Event {
    def typ = "deal"

    def data = Json.obj(
      "fen" -> fen,
      "seatIndexes" -> seatIndexes
    )
  }

  object Deal {
    def apply(
      situation: Situation, seatIndexes: Vector[Side]): Deal = Deal(
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

  case class Me(possibleMoves: Option[List[PlayerAct]], player: Player) extends Event {
    def typ = "me"

    def data = Me.json(possibleMoves, player)

    private def side = player.side

    override def only = Some(side)
  }

  object Me {

    def json(possibleMoves: Option[List[PlayerAct]], player: Player) = 
      Json.obj(
        "status" -> player.status,
        "side" -> player.side
      ).add("possibleMoves" -> possibleMoves.map(PossibleMoves.json))

    def json(masa: Masa, player: Player): JsObject = 
      json(masa.possibleMoves(player), player)

    def apply(masa: Masa, player: Player): Me = Me(
      possibleMoves = masa.possibleMoves(player),
      player = player)

  }

  object PossibleMoves {

    def json(acts: List[PlayerAct]) =
      if (acts.isEmpty) JsNull
      else {
        val sb = new java.lang.StringBuilder(128)
        var first = true
        acts foreach {
          case act =>
            if (first) first = false
            else sb append " "
            sb append act.uci
        }
        JsString(sb.toString)
      }

  }
}
