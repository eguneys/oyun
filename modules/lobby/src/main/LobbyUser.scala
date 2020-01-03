package oyun.lobby

import oyun.user.User

private[lobby] case class LobbyUser(
  id: User.ID,
  username: String) {


}


private[lobby] object LobbyUser {

  def make(user: User) = LobbyUser(
    id = user.id,
    username = user.username
  )

}
