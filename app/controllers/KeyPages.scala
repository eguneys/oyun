package controllers

import play.api.mvc._
import scalatags.Text.all.Frag

import oyun.api.Context
import oyun.app._
import views._

final class KeyPages(env: Env)(implicit ec: scala.concurrent.ExecutionContext) {

  def home(status: Results.Status)(implicit ctx: Context): Fu[Result] =
    funit
      .map { _ =>
        html.lobby.home()
      }
      .dmap { (html: Frag) =>
        status(html)
      }

  def notFound(ctx: Context): Result = {
    Results.NotFound(html.base.notFound()(ctx))
  }

}
