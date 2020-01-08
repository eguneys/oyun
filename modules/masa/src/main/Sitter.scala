package oyun.masa

import oyun.game.Side
import oyun.user.User

final private class Sitter(
)(implicit ec: scala.concurrent.ExecutionContext) {

  def sit(masa: Masa, userId: User.ID, side: Side)
    (implicit proxy: MasaProxy): Fu[Events] =
    if (masa.empty(side) && masa.sitable(userId)) {
      val prog = masa.sit(userId, side)
      proxy.save(prog) >>
      fuccess(prog.events)
    }
    else fufail(ClientError(s"$masa can't sit $userId"))

}
