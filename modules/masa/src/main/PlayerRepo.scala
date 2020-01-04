package oyun.masa

final class PlayerRepo(
)(implicit ctx: scala.concurrent.ExecutionContext) {

  private var players = Vector[Player]()

  def byMasa(masaId: Masa.ID): Fu[Players] =
    funit inject {
      players filter (_.masaId == masaId) toList
    }


}
