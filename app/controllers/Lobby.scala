package controllers

import play.api.libs.json._
import play.api.mvc._

import oyun.app._
import views._

final class Lobby(env: Env) extends OyunController(env) {

  private val lobbyJson = Json.obj(
    "lobby" -> Json.obj(
      "version" -> 0
    )
  )

  def home = Open { implicit ctx =>
    negotiate(
      html = keyPages.home(Results.Ok),
      api = _ =>
      env.api.lobbyApi.masasJson map { json =>
        Ok(lobbyJson ++ json)
      })
  }


}
