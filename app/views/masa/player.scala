package views.html
package masa

import play.api.libs.json.Json

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.common.String.html.safeJsonValue
import oyun.masa.Pov

object player {

  def apply(pov: Pov,
    data: play.api.libs.json.JsObject
  )(implicit ctx: Context) = {
    bits.layout(
      title = s"${trans.play.txt()}",
      moreJs = frag(
        masaTag,
        embedJsUnsafe(s"""oyunkeyf=window.oyunkeyf||{};onload=function() {
OyunkeyfMasa.boot(${safeJsonValue(
Json.obj(
"data" -> data,
"i18n" -> jsI18n(pov.game),
"userId" -> ctx.userId
)
)})
}""")
      ),
      playing = true
    )(
      main(cls := "masa")(
        st.aside(cls := "masa__side")(
          bits.side(pov, data)
        ),
        bits.masaAppPreload(pov)
      )
    )
  }
  
}
