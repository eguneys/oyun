package oyun.masa

import scala.util.Success

final private class MasaProxy(
  id: Masa.ID,
  dependencies: MasaProxy.Dependencies
)(implicit ec: scala.concurrent.ExecutionContext) {

  import dependencies._

  private[masa] def masa: Fu[Option[Masa]] = cache

  def save(progress: Progress): Funit = {
    set(progress.masa)
    masaRepo.update(progress)
  }

  private def set(masa: Masa): Unit = {
    cache = fuccess(masa.some)
  }

  def withMasa[A](f: Masa => Fu[A]): Fu[A] = cache.value match {
    case Some(Success(Some(g))) => f(g)
    case Some(Success(None)) => fufail(s"No proxy masa: $id")
    case _ =>
      cache flatMap {
        case None => fufail(s"No proxy masa: $id")
        case Some(g) => f(g)
      }
  }

  private[this] var cache: Fu[Option[Masa]] = fetch

  private[this] def fetch = masaRepo masa id

}

private object MasaProxy {

  class Dependencies(
    val masaRepo: MasaRepo
  )

}
