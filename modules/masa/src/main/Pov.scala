package oyun.masa

import oyun.game.{ Game, Side }

case class Pov(masa: Masa, side: Side, game: Option[Game] = None) {

  def player = masa player side

  def playerId = player map (_.id)

  def fullId = masa fullIdOf side

  def masaId = masa.id
 
  def withGame(game: Game) = copy(game = Some(game)) 
}

object Pov {

  def apply(masa: Masa): List[Pov] = masa.players.map { apply(masa, _) }

  def apply(masa: Masa, player: Player) = new Pov(masa, player.side)

  def apply(masa: Masa, playerId: Player.ID): Option[Pov] =
    masa player playerId map { apply(masa, _) }
}
