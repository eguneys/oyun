package controllers

import play.api.libs.json._
import play.api.mvc._

import oyun.api.Context
import oyun.app._
import oyun.game.{ Side }
import oyun.masa.{ Pov }
import views._


final class Masa(
  env: Env
) extends OyunController(env) {


  private def renderPov(pov: Pov)(implicit ctx: Context): Fu[Result] =
    negotiate(
      html = env.api.masaApi.player(pov) map {
        case data =>
          Ok(html.masa.player(
            pov,
            data
          ))
      },
      api = _ => ???
    )

  def watcher(masaId: String, side: String) = Open { implicit ctx =>
    OptionFuResult(proxyPov(masaId, side)) { pov =>
      renderPov(pov)
    }
  }

  private def proxyPov(masaId: String, side: String): Fu[Option[Pov]] = 
    env.masa.proxyRepo.pov(masaId, Side.orDefault(side))
  
}
