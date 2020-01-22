package oyun.game

import play.api.libs.json._

import ornicar.scalalib.Random

import oyun.user.{ User, Avatar }

import poker.{ Chips, Side }

case class Player(
  id: Player.ID, // random
  side: Side,
  userId: User.ID,
  avatar: Avatar,
  status: Player.Status,
  button: Boolean,
  stack: Chips
) {

  import Player._

  def sitoutNext(value: Boolean): Option[Player] = status match {
    case WaitOthers | WaitNextHand => None
    case _ => if (value)
      copy(status = SitOutNextHand).some
    else
      copy(status = Involved).some
  }

  def sitoutNext: Boolean = is(SitOutNextHand)

  def involved: Player = copy(status = Involved)

  def nextButton: Player = copy(button = !button)

  def buttonOn = copy(button = true)
  def buttonOff = copy(button = false)

  def is(s: Status): Boolean = s == status

}

object Player {

  sealed trait Status {
    val forsyth: String
  }

  case object WaitNextHand extends Status {
    val forsyth = "WN"
  }
  case object WaitOthers extends Status {
    val forsyth = "WO"
  }
  case object Involved extends Status {
    val forsyth = "I"
  }
  case object SitOutNextHand extends Status {
    val forsyth = "SN"
  }

  type ID = String

  def make(
    noPlayers: Boolean,
    side: Side,
    user: User,
    chips: Chips) = if (noPlayers)
    Player(side, user, chips, Player.WaitOthers, true)
  else
    Player(side, user, chips, Player.WaitNextHand, false)

  def apply(
    side: Side,
    user: User,
    stack: Chips,
    status: Status,
    button: Boolean) =
    new Player(id = makePlayerId,
      side = side,
      userId = user.id,
      avatar = user.avatar,
      status = status,
      button = button,
      stack = stack)

  def makePlayerId = Random nextString 4

  implicit val statusWriter: Writes[Status] = new Writes[Status] {
    def writes(status: Status) = JsString(status.forsyth)
  }

  implicit val avatarWriter: Writes[Avatar] = new Writes[Avatar] {
    def writes(avatar: Avatar) = JsString(avatar.link)
  }
}
