package oyun.game

import poker.{ Side }
import oyun.user.User

case class Pov(masa: Masa, side: Option[Side] = None) {

  def realSide = side | Side.ZeroI

  def player = masa player realSide

  def playerId = player map (_.id)

  def masaId = masa.id

}

object Pov {

  def apply(masa: Masa): List[Pov] = masa.players.map { apply(masa, _) }

  def apply(masa: Masa, player: Player) = new Pov(masa, Some(player.side))

  def apply(masa: Masa, userId: User.ID): Option[Pov] =
    masa player userId map { apply(masa, _) }
}
