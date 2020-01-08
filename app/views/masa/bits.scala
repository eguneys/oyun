package views.html
package masa

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.masa.{ Pov }
import oyun.game.Game

import controllers.routes

object bits {

  def layout(
    title: String,
    moreJs: Frag = emptyFrag,
    moreCss: Frag = emptyFrag,
    playing: Boolean = false
  )(body: Frag)(implicit ctx: Context) =
    views.html.base.layout(
      title = title,
      moreJs = moreJs,
      moreCss = frag(
        cssTag("masa"),
        moreCss
      ),
      playing = playing,
      deferJs = true)(body)


  private[masa] def side(
    pov: Pov,
    data: play.api.libs.json.JsObject
)(implicit ctx: Context) = views.html.game.side(
    pov)

  def masaAppPreload(pov: Pov)(implicit ctx: Context) =
    div(cls := "masa__app")(
    )

}
