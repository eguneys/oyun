package oyun.masa

import oyun.user.{ User }

private[masa] case class Player(
  _id: Player.ID, // random
  masaId: Masa.ID,
  userId: Option[User.ID]
) {

  def id = _id

}

object Player {

  type ID = String

}
