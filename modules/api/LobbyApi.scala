package oyun.api

import play.api.libs.json.{ JsArray, JsObject, Json }

import oyun.user.UserContext

final class LobbyApi(
  masaEnv: oyun.masa.Env,
)(implicit ec: scala.concurrent.ExecutionContext) {

  private def masaApi = masaEnv.api

  def apply(implicit ctx: Context): Fu[JsObject] =
    for {
      masas <- masaApi.fetchVisibleMasas
      masasJson <- masaEnv apiJsonView masas
    } yield {
      Json.obj(
        "me" -> ctx.me.map { u =>
          Json.obj("username" -> u.username)
        },
      ) ++ masasJson
    }
  
}
