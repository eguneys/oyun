package oyun.game

import poker.{ Status, Side, Game => PokerGame }

case class Game(poker: PokerGame, 
  status: Status,
  seatIndexes: Vector[Side]) {

  def situation = poker.situation
  def dealer = poker.dealer
  def clock = poker.clock

  def visual = poker.dealer.visual

  def sideToAct = seatIndexes(turnToAct)

  def turnToAct = poker.player

  def turnOf(s: Side) = s == sideToAct

  def playable = status < Status.OneWin

  def started = status >= Status.Started

  def playing = status < Status.OneWin

  def finished = status >= Status.OneWin

  def update(game: PokerGame) = copy(poker = game,
    status = game.situation.status | status
  )

  def start =
    if (started) this
    else
      copy(status = Status.Started)
  
}

object Game {


  def makeGame(blinds: Float, players: List[Player]): Game = {
    val poker = PokerGame(
      blinds,
      button = players.indexWhere(_.button) match {
        case -1 => 0
        case n => (n + 1) % players.length
      },
      iStacks = players.map(_.stack)
    )

    val seatIndexes = players.map(_.side).toVector

    Game(poker = poker,
      status = Status.Created,
      seatIndexes = seatIndexes)
  }
}
