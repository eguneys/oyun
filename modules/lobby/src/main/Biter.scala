package oyun.lobby

import actorApi.{ JoinMasa }
import oyun.socket.Socket.{ Sri }
import poker.{ Side }
import oyun.game.{ Masa }

final private class Biter(
//  masaRepo: oyun.game.MasaRepo
) { //(implicit ec: scala.concurrent.ExecutionContext) {

  def apply(masa: Masa, sri: Sri, lobbyUserOption: Option[LobbyUser]): Fu[JoinMasa] =
    funit inject JoinMasa(sri, masa, Side.ZeroI)

}
