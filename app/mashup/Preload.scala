package oyun.app
package mashup

import play.api.libs.json._

import oyun.api.Context

final class Preload(lobbyApi: oyun.api.LobbyApi)(implicit ec: scala.concurrent.ExecutionContext) {

  import Preload._

  def apply()(implicit ctx: Context): Fu[Homepage] =
    lobbyApi(ctx) flatMap {
      case data => fuccess(Homepage(data))
    }

}

object Preload {

  case class Homepage(data: JsObject)

}
