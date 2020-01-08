package oyun.masa

import akka.actor.{ ActorSystem }

import oyun.game.{ Masa }

final class MasaApi(
  masaRepo: oyun.game.MasaRepo
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {

  private[masa] def create(masa: Masa): Funit = {
    masaRepo.insert(masa).void
  }


  def fetchVisibleMasas: Fu[List[Masa]] =
    masaRepo.publicCreated map {
      case created =>
        created
    }

  
}
