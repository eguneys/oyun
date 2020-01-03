package oyun.round

import oyun.user.{ User }

private[round] case class Player(
  _id: Player.ID, // random
  roundId: Round.ID,
  userId: Option[User.ID]
) {

  def id = _id

}

object Player {

  type ID = String

}
