package oyun.masa

import poker.{ Status }
import oyun.game.{ Masa }

final private class Finisher(
) {

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
