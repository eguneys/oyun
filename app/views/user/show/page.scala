package views.html.user.show

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.User

import controllers.routes

object page {

  def activity(u: User)(implicit ctx: Context) =
    views.html.base.layout(
      title = s"${u.username} : ",
      moreJs = moreJs(),
      moreCss = frag(
        cssTag("user.show")
      )
    ) {
      main(cls := "page-menu", dataUsername := u.username)(
        st.aside(cls := "page-menu__menu")(side(u)),
        div(cls := "page-menu__content box user-show")(
          views.html.user.show.header(u),
          div(cls := "angle-content")
        )
      )
    }


  private def moreJs()(implicit ctx: Context) = frag(
    jsAt("compiled/user.js")
  )
  
  private val dataUsername = attr("data-username")
}
