package views.html.user.show

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.User

import controllers.routes

object side {

  def apply(u: User)(implicit ctx: Context) = {

    div(cls := "side sub-ratings")(
      
    )

  }
  
}
