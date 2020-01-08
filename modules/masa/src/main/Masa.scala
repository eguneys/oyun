package oyun.masa

import ornicar.scalalib.Random

import oyun.game.{ Game, Side, NbSeats }
import oyun.user.User

final case class Masa(
  id: Masa.ID,
  nbSeats: NbSeats,
  stakes: Masa.Stakes,
  seats: Vector[Option[Player]]
) {

  import Masa._

  val players = seats.flatten.toList

  def player(side: Side): Option[Player] = seats.lift(side.index).flatten

  def player(userId: User.ID): Option[Player] =
    players find(_.userId == userId)

  def fullIdOf(side: Side): String = s"$id${side.index}"

  def pov(s: Side) = Pov(this, s)

  def empty(s: Side) = player(s).isDefined

  def sitable(userId: User.ID) = !player(userId).isDefined

  def sit(userId: User.ID, side: Side) = {

  }

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
