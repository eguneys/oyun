package oyun.masa

import play.api.libs.json._

import oyun.user.LightUserApi
import oyun.game.{ Masa }

final class ApiJsonView(
  lightUserApi: LightUserApi
)(implicit ctx: scala.concurrent.ExecutionContext) {

  import JsonView._

  def apply(masas: List[Masa]): Fu[JsObject] =
    for {
      created <- masas.collect(visibleJson).sequenceFu
    } yield Json.obj(
      "masas" -> created
    )

  private def visibleJson: PartialFunction[Masa, Fu[JsObject]] = {
    case masa => fullJson(masa)
  }

  def fullJson(masa: Masa): Fu[JsObject] =
    for {
      playersJson <- playersJson(masa)
    } yield baseJson(masa) ++ playersJson

  def baseJson(masa: Masa): JsObject =
    Json
      .obj("id" -> masa.id,
        "stakes" -> masa.stakes,
        "nbSeats" -> masa.nbSeats.nb)

  def playersJson(masa: Masa): Fu[JsObject] =
    for {
      players <- fuccess(masa.players)
      fullPlayers <- players.map(JsonView.playerJson(lightUserApi)).sequenceFu
    } yield Json.obj(
      "players" -> fullPlayers
    )

}
