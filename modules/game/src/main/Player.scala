package oyun.game

import ornicar.scalalib.Random

import oyun.user.{ User }

case class Player(
  id: Player.ID, // random
  side: Side,
  userId: User.ID,
  status: Player.Status
) {

}

object Player {

  sealed trait Status {
    val forsyth: String
  }

  case object WaitNextHand extends Status {
    val forsyth = "WN"
  }
  case object Involved extends Status {
    val forsyth = "I"
  }
  case object SitOutNextHand extends Status {
    val forsyth = "SN"
  }

  type ID = String

  def apply(
    side: Side,
    user: User) =
    new Player(id = makePlayerId,
      side = side,
      userId = user.id,
      status = WaitNextHand)

  def makePlayerId = Random nextString 4
}
