package oyun.masa

import oyun.user.{ User }

private[masa] case class Player(
  id: Player.ID, // random
  side: Masa.Side,
  userId: Option[User.ID]
) {

}

object Player {

  type ID = String

}
