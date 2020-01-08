package oyun.masa

import ornicar.scalalib.Random

import oyun.game.{ Side }
import oyun.user.{ User }

private[masa] case class Player(
  id: Player.ID, // random
  side: Side,
  userId: User.ID,
  status: Player.Status
) {

}

object Player {

  type ID = String

  def apply(
    side: Side,
    userId: User.ID) = 
    new Player(id = makePlayerId,
      side = side,
      userId = userId)

  def makePlayerId = Random nextString 4
}
