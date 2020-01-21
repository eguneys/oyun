package oyun.masa

import poker.{ Side, Status }
import oyun.game.{ Masa, Game }
import oyun.user.User

final private class Finisher(
)(implicit ec: scala.concurrent.ExecutionContext) {

  def eject(masa: Masa)(implicit proxy: MasaProxy): Fu[Events] = {
    val prog = masa.eject
    proxy.save(prog)
    fuccess(prog.events)
  }

  def other(masa: Masa, status: Status.type => Status)(implicit proxy: MasaProxy): Fu[Events] =
    apply(masa, status)

  private def apply(masa: Masa, makeStatus: Status.type => Status)(implicit proxy: MasaProxy): Fu[Events] = {
    val status = makeStatus(Status)
    val prog = masa.finish(status)
    proxy.save(prog)
    funit inject {
      prog.events
    }
  }

}
