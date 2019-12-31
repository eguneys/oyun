package views.html.blog

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object show {

  def apply(doc: io.prismic.Document)(implicit ctx: Context, prismic: oyun.blog.BlogApi.Context) =
    views.html.base.layout(
      title = s"${~doc.getText("blog.title")} | Blog",
      moreCss = cssTag("blog")
    )(
      main(cls := "page-menu page-small")(
        bits.menu(none, false),
        div(cls := s"blog page-menu__content box post ${~doc.getText("blog.cssClasses")}")(
          h1(doc.getText("blog.title")),
          div(cls := "footer")(
            if (prismic.maybeRef.isEmpty) {
              (doc
                .getDate("blog.date")
                .exists(_.value.toDateTimeAtStartOfDay isAfter org.joda.time.DateTime.now.minusWeeks(2))) option
              span()
              // a(href := routes.Blog.discuss(doc.id), cls := "button text discuss", dataIcon := "d")(
              //   "Discuss this blog post in the forum"
              // )
            } else p("This is a preview.")
          )
        )
      )
    )

}
