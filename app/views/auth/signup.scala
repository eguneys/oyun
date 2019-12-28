package views.html
package auth

import play.api.data.Form

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object signup {

   private val recaptchaScript = raw(
    """<script src="https://www.google.com/recaptcha/api.js" async defer></script>"""
  )

  def apply(form: Form[_], recaptcha: oyun.security.RecaptchaPublicConfig)(implicit ctx: Context) =
    views.html.base.layout(
      title = trans.signUp.txt(),
      moreJs = frag(
        jsTag("signup.js"),
        recaptcha.enabled option recaptchaScript
      ),
      moreCss = cssTag("auth")
    ) {

      main(cls := "auth auth-signup box box-pad")(
        h1(trans.signUp()),
        postForm(id := "signup_form", cls := "form3", action := routes.Auth.signupPost)(
          auth.bits.formFields(form("username"), form("password"), form("email").some, register = true),
          input(id := "signup-fp-input", name := "fp", tpe := "hidden"),
          if (recaptcha.enabled)
            button(
              cls := "g-recaptcha submit button text big",
              attr("data-sitekey") := recaptcha.key,
              attr("data-callback") := "signupSubmit"
            )(trans.signUp())
          else
            form3.submit(trans.signUp(), icon = none, klass = "big")
        )
      )
    }
  
}
