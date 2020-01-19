package oyun.game

import poker.{ Side, Game => PokerGame }

case class Game(poker: PokerGame, seatIndexes: List[Side]) {

  def visual = poker.dealer.visual
  
}

object Game {


  def makeGame(blinds: Float, players: List[Player]): Game = {
    val poker = PokerGame(
      blinds,
      button = players.indexWhere(_.button) | 0,
      iStacks = players.map(_.stack)
    )

    val seatIndexes = players.map(_.side)

    Game(poker = poker,
      seatIndexes = seatIndexes)
  }
}
