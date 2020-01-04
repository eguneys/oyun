package oyun.masa

import akka.actor.{ ActorSystem }

final class MasaApi(
  masaRepo: MasaRepo
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {

  private[masa] def create(masa: Masa): Funit = {
    masaRepo.insert(masa).void
  }


  def fetchVisibleMasas: Fu[Masas] =
    masaRepo.publicCreated map {
      case created =>
        created
    }

  
}
