package views.html
package base

import oyun.api.Context
import oyun.app.ui.ScalatagsTemplate._
import oyun.app.templating.Environment._

import controllers.routes

object notFound {

  def apply()(implicit ctx: Context) =
    layout(
      title = "Page not found",
      moreCss = cssTag("not-found")
    ) {
      main(cls := "not-found page-small box box-pad")(
        header(
          h1("404"),
          div(
            strong("Page not found!"),
            p(
              "Return to ",
              a(href := routes.Lobby.home)("the homepage")
            )
          )
        )
      )

    }

}
