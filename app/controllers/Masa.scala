package controllers

import play.api.libs.json._
import play.api.mvc._

import oyun.api.Context
import oyun.app._
import poker.{ Side }
import oyun.game.{ Pov }
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
      api = _ => env.api.masaApi.player(pov) map {
        case data =>
          Ok {
            data
          }
      }
    )

  def watcher(masaId: String) = Open { implicit ctx =>
    OptionFuResult(proxyPov(masaId)) { pov =>
      renderPov(pov)
    }
  }

  private def proxyPov(masaId: String)(implicit ctx: Context): Fu[Option[Pov]] = 
    env.masa.proxyRepo.pov(masaId, ctx.me.map(_.id))
  
}
