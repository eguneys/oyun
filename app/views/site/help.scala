package views.html.site

import oyun.api.Context
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes

object help {

  def page(active: String, doc: io.prismic.Document, resolver: io.prismic.DocumentLinkResolver)(
    implicit ctx: Context) = {

    val title = ~doc.getText("doc.title")
    layout(
      title = title,
      active = active,
      contentCls = "page box box-pad",
      moreCss = cssTag("page")
    )(
      frag(
        h1(title),
        div(cls := "body")(raw(~doc.getHtml("doc.content", resolver)))
      )
    )
  }


  def source(doc: io.prismic.Document, resolver: io.prismic.DocumentLinkResolver)(implicit ctx: Context) = {
    val title = ~doc.getText("doc.title")
    layout(
      title = title,
      active = "source",
      moreCss = frag(cssTag("source")),
      contentCls = "page"
    )(
      frag(
        div(cls := "box box-pad body")(
          h1(title),
          raw(~doc.getHtml("doc.content", resolver))
        ),
        br,
        div(cls := "box")(freeJs())
      )
    )
  }


  def layout(
    title: String,
    active: String,
    contentCls: String = "",
    moreCss: Frag = emptyFrag,
    moreJs: Frag = emptyFrag
  )(body: Frag)(implicit ctx: Context) =
    views.html.base.layout(
      title = title,
      moreCss = moreCss,
      moreJs = moreJs
    ) {

      val sep = div(cls := "sep")
      val external = frag(" ", i(dataIcon := "0"))
      def activeCls(c: String) = cls:= active.activeO(c)

      main(cls := "page-menu")(
        st.nav(cls := "page-menu__menu subnav")(
          a(activeCls("about"), href := routes.Page.about)(trans.aboutX("oyunkeyf.org")),
          a(activeCls("faq"), href := routes.Main.faq)("FAQ"),
          a(activeCls("contact"), href := routes.Main.contact)(trans.contact()),
          a(activeCls("source"), href := routes.Page.source)(trans.sourceCode()),
          a(activeCls("help"), href := routes.Page.help)(trans.contribute()),
          a(activeCls("thanks"), href := routes.Page.thanks)(trans.thankYou())
        ),
        div(cls := s"page-menu__content $contentCls")(body)
      )


    }

}
