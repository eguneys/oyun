package oyun.masa

import poker.{ Side }
import oyun.game.{ Masa }
import oyun.user.User

final private class Dealer(
)(implicit ec: scala.concurrent.ExecutionContext) {

  def deal(masa: Masa)
    (implicit proxy: MasaProxy): Fu[Events] = {
    val prog = masa.deal
    proxy.save(prog) >>
    fuccess(prog.events)
  }


}
