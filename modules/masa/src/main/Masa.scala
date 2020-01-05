package oyun.masa

import ornicar.scalalib.Random

import Masa._

final case class Masa(
  id: Masa.ID,
  nbSeats: NbSeats,
  stakes: Stakes,
  seats: Vector[Option[Player]]
) {

  import Masa._

  val players = seats.flatten.toList

  def player(side: Side): Option[Player] = seats.lift(side).flatten

  def player(playerId: Player.ID): Option[Player] =
    players find(_.id == playerId)

  def fullIdOf(side: Side): String = s"$id${side}"

  def pov(s: Side) = Pov(this, s)

}

object Masa {

  type Side = Int

  type ID = String

  def scheduleAs(sched: Schedule) = Masa(
    id = makeId,
    nbSeats = sched.nbSeats,
    stakes = sched.stakes,
    seats = Vector.fill(sched.nbSeats.nb)(None)
  )

  sealed trait NbSeats {
    val nb: Int
  }
  case object Five extends NbSeats {
    val nb = 5
  }
  case object Nine extends NbSeats {
    val nb = 9
  }

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