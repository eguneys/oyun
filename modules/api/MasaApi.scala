package oyun.api

import play.api.libs.json._
import oyun.game.Game
import oyun.masa.{ Pov, JsonView }

final private[api] class MasaApi(
  jsonView: JsonView
)() {

  def player(pov: Pov)(implicit ctx: Context): Fu[JsObject] =
    jsonView.playerJson(
      pov
    )
  
}
