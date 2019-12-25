package views.html.base

import play.api.i18n.Lang
import play.api.libs.json.Json

import oyun.api.{ Context }
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._

import controllers.routes


object layout {


  object bits {

    val doctype = raw("<!DOCTYPE html>")
    def htmlTag(implicit lang: Lang) = html(st.lang := lang.code)
    val topComment = raw("""<!-- Oyunkeyf is open source! See https://github.com/eguneys/oyun --->""")
    val charset = raw("""<meta charset="utf-8">""")
    val viewport = raw(
      """<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">"""
    )

  }

  import bits._

  private def fontPreload(implicit ctx: Context) = raw {
    s"""<link rel="preload" href="${assetUrl(s"font/oyunkeyf.woff2")}" as="font" type="font/woff2" crossorigin>"""

  }

  private val favicons = raw {
    List(16) map { px =>
      s"""<link rel="icon" type="image/png" href="${staticUrl(s"logo/oyunkeyf-favicon-$px.png")}" sizes="${px}x${px}">"""
    } mkString("", "", s"""<link id="favicon" rel="icon" type="image/png" href="${staticUrl("logo/oyunkeyf-favicon-32.png")}" sizes="32x32">""")
  }

  private val spaceRegex = """\s{2,}+""".r
  private def spaceless(html: String) = raw(spaceRegex.replaceAllIn(html.replace("\\n", ""), ""))

  def apply(
    title: String,
    fullTitle: Option[String] = None,
    moreCss: Frag = emptyFrag,
    moreJs: Frag = emptyFrag,
    playing: Boolean = false,
    deferJs: Boolean = false
  )(body: Frag)(implicit ctx: Context): Frag = frag(
    doctype,
    htmlTag(ctx.lang)(
      topComment,
      head(
        charset,
        viewport,
        st.headTitle {
          if (isProd) fullTitle | s"$title * oyunkeyf.net"
          else s"[dev] ${fullTitle | s"$title * oyunkeyf.dev"}"
        },
        cssTag("site"),
        moreCss,
        favicons,
        fontPreload
      ),
      st.body(
        cls := List(
          "mobile" -> false
        ),
        dataDev := (!isProd).option("true")
      )(
        siteHeader(playing),
        div(
          id := "main-wrap",
          cls := List(
            "is2d" -> true
          )
        )(body),
        a(id := "reconnecting", cls := "link text", dataIcon := "B")("trans.reconnecting"),
        if (isProd)
          jsAt(s"compiled/oyunkeyf.site.min.js", defer = deferJs)
        else
          frag(
            jsAt(s"compiled/oyunkeyf.deps.js", defer = deferJs),
            jsAt(s"compiled/oyunkeyf.site.js", defer = deferJs),
          ),
        moreJs
      )
    )
  )

  object siteHeader {

    private val topnavToggle = spaceless(
      """
<input type="checkbox" id="tn-tg" class="topnav-toggle fullscreen-toggle">
"""
    )

    def apply(playing: Boolean)(implicit ctx: Context) =
      header(id := "top")(
        div(cls := "site-title-nav")(
          topnavToggle,
          h1(cls := "site-title")(
            a(href := "/")(
              "oyunkeyf",
              span(if(isProd) ".net" else ".dev")
            )
          ),
          topnav()
        ),
        div(cls := "site-buttons")(
        )
      )

  }
}
