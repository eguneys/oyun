package oyun.api

import play.api.libs.json.{ JsArray, JsObject, Json }

import oyun.user.UserContext

final class LobbyApi()(implicit ec: scala.concurrent.ExecutionContext) {

  def apply(implicit ctx: Context): Fu[JsObject] =
    funit inject {
      Json.obj(
        "me" -> ctx.me.map { u =>
          Json.obj("username" -> u.username)
        }
      )
    }
  
}
