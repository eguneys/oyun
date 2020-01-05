package oyun.masa

final class MasaRepo() {

  private var masas = Vector[Masa]()

  def publicCreated: Fu[List[Masa]] =
    fuccess(masas.toList)
  

  def insert(masa: Masa): Fu[Masa] = {
    masas = masas.filterNot(_.id == masa.id) :+ masa
    fuccess(masa)
  }

  def delete(masa: Masa): Fu[Unit] = {
    masas = masas.filterNot(_.id == masa.id)
    funit
  }

  def byId(masaId: Masa.ID): Fu[Option[Masa]] =
    fuccess(masas.find(_.id == masaId))
}
