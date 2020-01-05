package oyun.lobby

import actorApi.{ JoinMasa }
import oyun.socket.Socket.{ Sri }
import oyun.masa.Masa

final private class Biter(
  masaRepo: oyun.masa.MasaRepo
)(implicit ec: scala.concurrent.ExecutionContext) {

  def apply(masa: Masa, sri: Sri, lobbyUserOption: Option[LobbyUser]): Fu[JoinMasa] =
    funit inject JoinMasa(sri, masa, 0)

}