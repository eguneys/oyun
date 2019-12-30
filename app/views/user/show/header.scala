package views.html.user.show

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.User

import controllers.routes

object header {

  def apply(u: User)(implicit ctx: Context) = frag(
    div(cls := "box__top user-show__header")(
      h1(cls := s"user-link ${if (isOnline(u.id)) "online" else "offline"}")(
        userSpan(u, withPowerTip = false)
      )
    )
  )

}
