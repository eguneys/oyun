package oyun.masa

import oyun.game.{ Pov, Masa, Side }
import oyun.user.User

final class MasaProxyRepo(
  masaSocket: MasaSocket
)(implicit ec: scala.concurrent.ExecutionContext) {

  def masa(masaId: Masa.ID): Fu[Option[Masa]] = masaSocket.getMasa(masaId)

  def pov(masaId: Masa.ID, userId: Option[User.ID]): Fu[Option[Pov]] =
    masa(masaId) dmap {
      _ map { case masa =>
        userId.fold(Pov(masa, None)){ masa.pov(_) }
      }
    }
  
}
