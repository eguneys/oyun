package oyun.masa

import poker.{ Side }
import oyun.game.{ Masa }
import oyun.user.User

final private class Sitter(
  userRepo: oyun.user.UserRepo
)(implicit ec: scala.concurrent.ExecutionContext) {

  def buyin(masa: Masa, userId: User.ID, side: Side)
    (implicit proxy: MasaProxy): Fu[Events] =
    if (masa.sitable(userId, side)) {
      userRepo byId userId flatMap { _ ?? { user =>
        val prog = masa.buyin(user, side)
        proxy.save(prog) >>
        fuccess(prog.events)
      } }
    }
    else fufail(ClientError(s"$masa can't sit $userId"))


  def sitoutNext(masa: Masa, side: Side, value: Boolean)
    (implicit proxy: MasaProxy): Fu[Events] = {
    val prog = masa.sitoutNext(side, value)
    proxy.save(prog) >>
    fuccess(prog.events)
  }

}
