package oyun.app
package templating

import controllers.routes

import oyun.api.Context
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.{ User, UserContext }

trait UserHelper { self: I18nHelper =>

  def isOnline(userId: String) = true


  def userSpan(user: User,
    cssClass: Option[String] = None,
    withOnline: Boolean = true,
    withPowerTip: Boolean = true,
    text: Option[String] = None): Frag =
    span(
      cls := userClass(user.id, cssClass, withOnline, withPowerTip),
      dataHref := userUrl(user.username)
    )(
      text | user.username
    )

  private def userUrl(username: String, params: String = "") =
    s"""${routes.User.show(username)}$params"""
  

  protected def userClass(
    userId: String,
    cssClass: Option[String],
    withOnline: Boolean,
    withPowerTip: Boolean = true): List[(String, Boolean)] =
    (withOnline ?? List((if (isOnline(userId)) "online" else "offline") -> true)) ::: List(
      "user-link" -> true,
      ~cssClass -> cssClass.isDefined,
      "ulpt" -> withPowerTip
    )

}
