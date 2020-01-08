package oyun.masa

import play.api.libs.json._
import scala.concurrent.ExecutionContext

import oyun.game.{ Masa, Pov, Player => GamePlayer }
import oyun.user.{ User, UserRepo }

final class JsonView(
  userRepo: UserRepo
)(implicit ec: ExecutionContext) {

  import JsonView._

  def playerJson(pov: Pov): Fu[JsObject] = 
    (pov.masa.seats.map{ _ ?? 
      { p => userRepo.byId(p.userId) }
    }.sequenceFu) map {
      case playerUsers =>
        import pov._
        Json.obj(
          "nbSeats" -> masa.nbSeats.nb,
          "stakes" -> masa.stakes.stakesString,
          "seats" -> (masa.seats zip playerUsers).map{
            case Some(p) ~ Some(u) => playerView(p, u)
            case _ => JsNull
          },
          "url" -> Json.obj(
            "socket" -> s"/play/$masaId",
            "round" -> s"/$masaId"
          )
        )
    }

  def playerView(player: GamePlayer, user: User): JsObject = Json.obj(
    "img" -> "https://placehold.it/200"
  )

}

object JsonView {

  def playerJson(
    lightUserApi: oyun.user.LightUserApi
  )(player: GamePlayer)(implicit ec: ExecutionContext): Fu[JsObject] = 
    for {
      light <- lightUserApi.async(player.userId)
    } yield Json
      .obj("id" -> player.id)
      .add("light" -> light)

  implicit val stakesWrites: OWrites[Masa.Stakes] = OWrites { stakes =>
    Json.obj(
      "stakes" -> stakes.stakesString,
      "buyIn" -> stakes.buyIn
    )
  }
  
}
