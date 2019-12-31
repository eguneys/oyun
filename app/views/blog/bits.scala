package views.html.blog

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object bits {

  private[blog] def menu(year: Option[Int], hasActive: Boolean = true) =
    st.nav(cls := "page-menu__menu subnav")(
      // a(cls := (year.isEmpty && hasActive).option("active"), href := routes.Blog.index())("Latest")
      
    )
  
}
