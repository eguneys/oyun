package views
package html.site

import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

object freeJs {

  def apply(): Frag = frag(
    div(cls := "box__top")(
      h1("Javascript modules")
    ),
    p(cls := "box__pad")(
      
    )
  )

}
