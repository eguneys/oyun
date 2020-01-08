package oyun.masa

import oyun.game.{ Pov, Masa, Side }

final class MasaProxyRepo(
  masaSocket: MasaSocket
)(implicit ec: scala.concurrent.ExecutionContext) {

  def masa(masaId: Masa.ID): Fu[Option[Masa]] = masaSocket.getMasa(masaId)

  def pov(masaId: Masa.ID, side: Side): Fu[Option[Pov]] =
    masa(masaId) dmap {
      _ flatMap { case masa =>
        masa.nbSeats.valid(side) option
        Pov(masa, side)
      }
    }
  
}
