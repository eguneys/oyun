package oyun.game

import poker.{ Side, Game => PokerGame }

case class Game(poker: PokerGame, seatIndexes: Vector[Side]) {

  def situation = poker.situation
  def dealer = poker.dealer
  def clock = poker.clock

  def visual = poker.dealer.visual

  def sideToAct = seatIndexes(turnToAct)

  def turnToAct = poker.player

  def turnOf(s: Side) = s == sideToAct

  def update(game: PokerGame) = copy(poker = game)
  
}

object Game {


  def makeGame(blinds: Float, players: List[Player]): Game = {
    val poker = PokerGame(
      blinds,
      button = players.indexWhere(_.button) | 0,
      iStacks = players.map(_.stack)
    )

    val seatIndexes = players.map(_.side).toVector

    Game(poker = poker,
      seatIndexes = seatIndexes)
  }
}
