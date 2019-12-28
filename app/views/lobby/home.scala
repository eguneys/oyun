package views.html.lobby

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object home {

  def apply()(implicit ctx: Context) = {

    views.html.base.layout(
      title = "",
      fullTitle = Some {
        s"oyunkeyf.net • ${trans.freeOnlinePoker.txt()}"
      },
      moreJs = frag(
        jsAt(s"compiled/oyunkeyf.lobby${isProd ?? (".min")}.js", defer = true),
        embedJsUnsafe(
          s"""oyunkeyf=window.oyunkeyf||{};
"""
        )
      ),
      moreCss = cssTag("lobby"),
    ) {

      main(
        cls := List("lobby" -> true)
      )(
      )
    }
  }
}
