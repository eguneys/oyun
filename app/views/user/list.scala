package views.html
package user

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.User

import controllers.routes

object list {

  def apply()(implicit ctx: Context) =
    views.html.base.layout(
      title = trans.players.txt(),
      moreCss = cssTag("user.list")
    ) {
      main(cls := "page-menu")(
        div(cls := "community page-menu__content box box-pad")(
          st.section(cls := "community__online")(
            h2(trans.onlinePlayers()),
            ol(cls := "user-top")()
          )
        )
      )
    }
  
}
