package views.html.base

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object topnav {

  private def linkTitle(url: String, name: Frag)(implicit ctx: Context) =
    a(href := url)(name)

  def apply()(implicit ctx: Context) = st.nav(id := "topnav", cls := "hover")(
    st.section(
      linkTitle(
        "/",
        frag(span(cls:= "home")("oyunkeyf.net")
        )
      ),
      div(role := "group")(

      )
    )
  )
  
}
