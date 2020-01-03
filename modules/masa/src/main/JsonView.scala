package oyun.masa

import play.api.libs.json._
import scala.concurrent.ExecutionContext

final class JsonView(
)(implicit ec: ExecutionContext) {

  import JsonView._


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
  
}
