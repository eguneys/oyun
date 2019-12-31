package views
package html.site

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object faq {


  private def question(id: String, title: String, answer: Frag) =
    div(
      st.id := id,
      cls := "question")(
      h3(a(href := s"#$id")(title)),
        div(cls := "answer")(answer)
    )

  def apply()(implicit ctx: Context) =
    help.layout(
      title = "Frequenty Asked Questions",
      active = "faq",
      moreCss = cssTag("faq")
    ) {

      main(cls := "faq small-page box box-pad")(
        h1(cls := "oyunkeyf_title")("Frequently Asked Questions"),
        h2("Oyunkeyf"),
        question("name",
          "Why is Oyunkeyf called Oyunkeyf?",
          p(
            "Oyun means game and keyf means pleasure in Turkish"
          )
        )
      )
      
    }

}
