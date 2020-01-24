package oyun.game

import play.api.libs.json._

import Player._

import poker.{ 
  PlayerAct,
  DealerAct,
  MiddleCards,
  Winners,
  PlayerDiff,
  PotDistribution,
  Hand,
  Pot,
  Card,
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
    act: PlayerAct,
    dAct: DealerAct,
    pDiff: PlayerDiff,
  ) extends Event {
    def typ = "move"

    def data = Json.obj(
      "uci" -> act.uci,
      "newStack" -> pDiff.newStack,
      "newWager" -> pDiff.newWager,
      "newRole" -> pDiff.newRole.forsyth.toString
    ) ++ Move.dAct(dAct)
    


  }

  object Move {

    def dAct(act: DealerAct) = act match {
      case poker.NextTurn(toAct) =>
        Json.obj(
          "nextTurn" -> Json.obj(
            "toAct" -> toAct
          )
        )
      case poker.NextRound(toAct, middle, runningPot, sidePots) =>
          Json.obj(
            "nextRound" -> Json.obj(
              "toAct" -> toAct,
              "middle" -> middleJson(middle),
              "pots" -> potsJson(runningPot :: sidePots)
            )
          )
      case poker.OneWin(winners) => Json.obj(
        "oneWin" -> Json.obj(
          "winners" -> winnersJson(winners)
        )
      )
      case poker.Showdown(middle, hands, winners) => Json.obj(
        "showdown" -> Json.obj(
          "winners" -> winnersJson(winners),
          "middle" -> middleJson(middle),
          "hands" -> HandDealer.handsJson(hands)
        )
      )
    }


    def winnersJson(winners: Winners) = Json.obj(
      "pots" -> potDistJson(winners.pots),
      "stacks" -> winners.stacks
    )

    def middleJson(middle: MiddleCards) = Json.obj(
    ).add("flop" -> middle.flop.map(HandDealer.cardsJson))
      .add("turn" -> middle.turn.map(HandDealer.cardJson))
      .add("river" -> middle.river.map(HandDealer.cardJson))

    def potDistJson(pots: List[PotDistribution]) = pots map(_.visual) mkString ("~")

    def potsJson(pots: List[Pot]) = pots map(_.visual) mkString ("~")

    def apply(
      move: PokerMove): Move = 
      Move(move.playerAct,
        move.dealerAct,
        move.playerDiff)

  }

  object HandDealer {

    def handsJson(hands: List[Option[Hand]]) = hands map {
      _.fold[JsValue](JsNull) { hand => Json.obj(
        "hole" -> hand.holeString,
        "rank" -> hand.value.key,
        "hand" -> hand.toString
      )}
    }

    def cardsJson(cards: List[Card]) = cards map(cardJson) mkString (" ")

    def cardJson(card: Card) = card.visual

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
      )})
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

  case class Me(possibleMoves: Option[List[PlayerAct]], hand: Option[List[Card]], player: Player) extends Event {
    def typ = "me"

    def data = Me.json(possibleMoves, hand, player)

    private def side = player.side

    override def only = Some(side)
  }

  object Me {

    def json(possibleMoves: Option[List[PlayerAct]], hand: Option[List[Card]], player: Player) = 
      Json.obj(
        "status" -> player.status,
        "side" -> player.side
      ).add("possibleMoves" -> possibleMoves.map(PossibleMoves.json))
        .add("hand" -> hand.map(HandDealer.cardsJson))

    def json(masa: Masa, player: Player): JsObject = 
      json(masa.possibleMoves(player), masa.handOf(player), player)

    def apply(masa: Masa, player: Player): Me = Me(
      possibleMoves = masa.possibleMoves(player),
      hand = masa.handOf(player),
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
