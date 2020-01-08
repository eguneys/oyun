package oyun.game

import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters._

final class MasaRepo() {

  private var masas = new ConcurrentHashMap[Masa.ID, Masa]()

  def publicCreated: Fu[List[Masa]] =
    fuccess(masas.values.asScala.toList)
  

  def update(progress: Progress): Funit =
    saveDiff(progress.origin, progress.masa)

  private def saveDiff(origin: Masa, masa: Masa): Funit = {
    masas.put(origin.id, masa)
    funit
  }

  def insert(masa: Masa): Fu[Masa] = {
    masas.put(masa.id, masa)
    fuccess(masa)
  }

  def delete(masa: Masa): Fu[Unit] = {
    masas.remove(masa.id)
    funit
  }

  def masa = byId _

  def byId(masaId: Masa.ID): Fu[Option[Masa]] =
    fuccess(Option(masas.get(masaId)))
}
