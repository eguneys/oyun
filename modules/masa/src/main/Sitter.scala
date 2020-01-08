package oyun.masa

import oyun.game.{ Masa, Side }
import oyun.user.User

final private class Sitter(
  userRepo: oyun.user.UserRepo
)(implicit ec: scala.concurrent.ExecutionContext) {

  def sit(masa: Masa, userId: User.ID, side: Side)
    (implicit proxy: MasaProxy): Fu[Events] =
    if (masa.sitable(userId, side)) {
      userRepo byId userId flatMap { _ ?? { user =>
        val prog = masa.sit(user, side)
        proxy.save(prog) >>
        fuccess(prog.events)
      } }
    }
    else fufail(ClientError(s"$masa can't sit $userId"))

}
