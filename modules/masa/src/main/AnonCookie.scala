package oyun.masa

import play.api.libs.json._

import oyun.game.Pov

object AnonCookie {

  val name = "rk2"
  val maxAge = 604800

  def json(pov: Pov): Option[JsObject] =
    !pov.player.map(_.userId).isDefined option Json.obj(
      "name" -> name,
      "maxAge" -> maxAge,
      "value" -> pov.playerId
    )
  
}
