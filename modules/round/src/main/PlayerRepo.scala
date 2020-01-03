package oyun.round

final class PlayerRepo(
)(implicit ctx: scala.concurrent.ExecutionContext) {


  def byMasa(masaId: Masa.ID): Fu[Players] = ???


}
