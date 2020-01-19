package oyun.game

import ornicar.scalalib.Random

import poker.{ NbSeats, Side, Move, Game => PokerGame }

import oyun.user.User

final case class Masa(
  id: Masa.ID,
  nbSeats: NbSeats,
  stakes: Masa.Stakes,
  seats: Vector[Option[Player]],
  game: Option[Game] = None
) {

  import Masa._

  val players = seats.flatten.toList

  def player(side: Side): Option[Player] = seats.lift(side.index).flatten

  def player(userId: User.ID): Option[Player] =
    players find(_.userId == userId)

  def sideOf(userId: User.ID): Option[Side] = player(userId).map(_.side)

  def pov(userId: User.ID) = Pov(this, sideOf(userId))


  def player: Option[Player] = game.flatMap { g => player(g.sideToAct) }

  def turnOf(p: Player) = player.exists(_==p)
  def turnOf(s: Side) = game.exists { _.turnOf(s) }

  def playableBy(s: Side) = turnOf(s)


  def possibleMoves(player: Player) = game.flatMap { game =>
    turnOf(player) option game.situation.possibleActs
  }


  def valid(s: Side) = nbSeats.valid(s)

  def empty(s: Side) = !player(s).isDefined

  def sitting(userId: User.ID) = player(userId).isDefined

  def sitable(userId: User.ID, side: Side) = 
    valid(side) && empty(side) && !sitting(userId)

  def noPlayers = players.length == 0
  def atLeastTwo = players.length >= 2

  def gameInProgress = game.isDefined

  def noGameInProgress = !gameInProgress

  def dealable = noGameInProgress && atLeastTwo

  def update(pokerGame: PokerGame, move: Move): Progress = {
    val updated = copy(
      game = game.map(_.update(pokerGame))
    )

    val events = Event.Move(move, pokerGame.situation) :: Nil
    Progress(this, updated, events)
  }

  def deal: Progress = {

    val game = Game.makeGame(stakes.blinds, players)

    val updated = copy(
      seats = seats.map {
        case Some(p) => Some(p.involved)
        case _ => None
      },
      game = Some(game)
    )

    val events = Event.Deal(
      game.poker.situation,
      game.seatIndexes
    ) :: players.map { Event.Me(updated, _) }

    Progress(this, updated, events)
  }

  def buyin(user: User, side: Side): Progress = {

    val p = if (noPlayers)
      Player(side, user, Player.WaitOthers, true, 10f)
    else
      Player(side, user, Player.WaitNextHand, false, 10f)

    val updated = updatePlayer(side, Some(p))
    Progress(this, updated) ++ List(
      Event.BuyIn(side, p),
      Event.Me(updated, p)
    )
  }

  def sitoutNext(side: Side, value: Boolean): Progress = {
    val oP = player(side) flatMap { _.sitoutNext(value) }

    val updated = updatePlayer(side, oP)
    Progress(this, updated) ++ List(
      Event.SitoutNext(side, oP)
    )
  }

  private def updatePlayer(side: Side, p: Option[Player]) = 
    copy(seats = seats.updated(side.index, p))



}

object Masa {

  type ID = String

  case class Id(value: String) extends AnyVal with StringValue {
    def full(userId: User.ID) = FullId(s"$value$userId")
  }

  case class FullId(value: String) extends AnyVal with StringValue {
    def masaId = Id(value take masaIdSize)
    def userId = value drop masaIdSize
  }

  val masaIdSize = 8

  def scheduleAs(sched: Schedule) = Masa(
    id = makeId,
    nbSeats = sched.nbSeats,
    stakes = sched.stakes,
    seats = Vector.fill(sched.nbSeats.nb)(None)
  )

  sealed trait Stakes {
    val blinds: Float

    def stakesString = f"$blinds%1.2f"
    def buyIn = blinds * 20
  }

  case object Micro1 extends Stakes {
    val blinds = 0.05f
  }
  case object Micro2 extends Stakes {
    val blinds = 0.1f
  }
  case object Micro3 extends Stakes {
    val blinds = 0.25f
  }
  case object Micro4 extends Stakes {
    val blinds = 0.5f
  }
  case object Mini1 extends Stakes {
    val blinds = 1.0f
  }
  case object Mini2 extends Stakes {
    val blinds = 2.0f
  }
  case object Mini5 extends Stakes {
    val blinds = 5.0f
  }
  case object Mini10 extends Stakes {
    val blinds = 10.0f
  }

  def makeId = Random nextString 8

}
