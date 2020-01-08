package oyun.api

import play.api.libs.json._
import oyun.game.{ Pov, Game }
import oyun.masa.{ JsonView }

final private[api] class MasaApi(
  jsonView: JsonView
)() {

  def player(pov: Pov)(implicit ctx: Context): Fu[JsObject] =
    jsonView.playerJson(
      pov
    )
  
}
