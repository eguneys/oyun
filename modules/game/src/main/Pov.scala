package oyun.game

import poker.{ Side }

case class Pov(masa: Masa, side: Option[Side] = None) {

  def realSide = side | Side.ZeroI

  def player = masa player realSide

  def playerId = player map (_.id)

  def masaId = masa.id

}

object Pov {

  def apply(masa: Masa): List[Pov] = masa.players.map { apply(masa, _) }

  def apply(masa: Masa, player: Player) = new Pov(masa, Some(player.side))

  def apply(masa: Masa, playerId: Player.ID): Option[Pov] =
    masa player playerId map { apply(masa, _) }
}
