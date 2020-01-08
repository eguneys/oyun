package oyun.masa

import play.api.libs.json._
import scala.concurrent.ExecutionContext

final class JsonView(
)(implicit ec: ExecutionContext) {

  import JsonView._

  def playerJson(pov: Pov): Fu[JsObject] = 
    funit map {
      case _ =>
        import pov._
        Json.obj(
          "masa" -> masaJsonView(masa),
          "url" -> Json.obj(
            "socket" -> s"/play/$fullId",
            "round" -> s"/$fullId"
          )
        )
    }

  def masaJsonView(masa: Masa): JsObject = Json.obj(
    "nbSeats" -> masa.nbSeats.nb,
    "stakes" -> masa.stakes.stakesString,
    "seats" -> masa.seats.map{ _.fold[JsValue](JsNull)(playerView) }
  )

  def playerView(player: Player): JsObject = Json.obj(
    "img" -> "https://placehold.it/200"
  )

}

object JsonView {

  def playerJson(
    lightUserApi: oyun.user.LightUserApi
  )(player: Player)(implicit ec: ExecutionContext): Fu[JsObject] = 
    for {
      light <- player.userId ?? lightUserApi.async
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
