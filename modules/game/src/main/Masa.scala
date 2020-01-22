package oyun.game

import ornicar.scalalib.Random

import poker.{ NbSeats, Status, Chips, Side, Move, Game => PokerGame }

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

  def playable = game.exists { _.playable }

  def playableBy(s: Side) = playable && turnOf(s)


  def possibleMoves(player: Player) = game.flatMap { game =>
    turnOf(player) option game.situation.possibleActs
  }

  def handOf(player: Player) = game.flatMap(_.handOf(player.side))


  def valid(s: Side) = nbSeats.valid(s)

  def empty(s: Side) = !player(s).isDefined

  def sitting(userId: User.ID) = player(userId).isDefined

  def sitable(userId: User.ID, side: Side) = 
    valid(side) && empty(side) && !sitting(userId)

  def nbPlayers = players.length
  def atLeastTwo = nbPlayers >= 2
  def headsUp = nbPlayers == 2
  def onePlayer = nbPlayers == 1
  def noPlayers = nbPlayers == 0

  def isGame = game.isDefined
  def noGame = !isGame
  def playing = game.exists(_.playing)
  def finished = game.exists(_.finished)

  def dealable = noGame && atLeastTwo

  def eject = {
    Progress(
      this,
      copy(
        game = None,
        seats = seats.map {
          case Some(p) => None
          case _ => None
        }
      ))
  }


  def finish(status: Status) = {
    val newSeats = seats.map {
      case Some(p) if p.sitoutNext => None
      case Some(p) => Some(p)
      case _ => None
    }

    val updated = copy(
      game = None,
      seats = newSeats
    )


    val events = (seats zip newSeats).foldLeft[List[Event]](Nil) {
      case (events, Some(p) ~ None) => Event.SitoutNext(p.side, None) :: events
      case (events, _) => events
    }


    Progress(
      this,
      updated,
      events)
  }

  def update(pokerGame: PokerGame, move: Move): Progress = {
    val updated = copy(
      game = game.map(_.update(pokerGame))
    )

    val events = Event.Move(move, pokerGame.situation) :: 
      updated.players.map { Event.Me(updated, _) 
}
    Progress(this, updated, events)
  }

  def deal: Progress = {

    val oldButton = players.indexWhere(_.button)
    val newButton = (oldButton + 1) % players.length


    val game = Game.makeGame(stakes.blinds, newButton, players)

    val updated = copy(
      seats = seats.zipWithIndex.map {
        case Some(p) ~ i if i == oldButton => Some(p.involved.nextButton)
        case Some(p) ~ i if i == newButton => Some(p.involved.nextButton)
        case Some(p) ~ _ => Some(p.involved)
        case _ => None
      },
      game = Some(game)
    )

    val events = Event.Deal(
      game.poker.situation,
      game.seatIndexes
    ) :: updated.players.map { Event.Me(updated, _) }

    Progress(this, updated, events)
  }

  def buyin(user: User, side: Side): Progress = {

    val p = Player.make(noPlayers, side, user, Chips(10f))

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
    val blinds: Chips

    def buyIn = blinds * 20
  }

  case object Micro1 extends Stakes {
    val blinds = Chips(0.05f)
  }
  case object Micro2 extends Stakes {
    val blinds = Chips(0.1f)
  }
  case object Micro3 extends Stakes {
    val blinds = Chips(0.25f)
  }
  case object Micro4 extends Stakes {
    val blinds = Chips(0.5f)
  }
  case object Mini1 extends Stakes {
    val blinds = Chips(1.0f)
  }
  case object Mini2 extends Stakes {
    val blinds = Chips(2.0f)
  }
  case object Mini5 extends Stakes {
    val blinds = Chips(5.0f)
  }
  case object Mini10 extends Stakes {
    val blinds = Chips(10.0f)
  }

  def makeId = Random nextString 8

}
