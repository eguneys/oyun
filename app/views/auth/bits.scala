package views.html
package auth

import play.api.data.{ Field, Form }

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.user.User

import controllers.routes

object bits {

  def formFields(username: Field, password: Field, emailOption: Option[Field], register: Boolean)
(implicit ctx: Context) = frag(
    form3.group(username, if (register) trans.username() else trans.usernameOrEmail()) { f =>
      frag(
        form3.input(f)(autofocus, required),
        p(cls := "error exists none")(trans.usernameAlreadyUsed())
      )
    },
  form3.password(password, trans.password()),
  emailOption.map { email =>
    form3.group(email, trans.email(), help = frag("We will only use it for password reset.").some)(
      form3.input(_, typ = "email")(required)
    )
  }
  )
  
}
