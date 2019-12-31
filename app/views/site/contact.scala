package views
package html.site

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object contact {

  def apply()(implicit ctx: Context) =
    help.layout(
      title = "Contact",
      active = "contact",
      moreCss = cssTag("contact"),
      contentCls = "page box box-pad"
    )(
      frag(
        h1("Contact Oyunkeyf"),
        div(cls := "contact")(
          a(href := s"mailto:oyunkeyfpoker@gmail.com")(s"oyunkeyfpoker@gmail.com")
          // renderedMenu
        )
      )
    )

}
