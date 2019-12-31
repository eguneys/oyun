package views.html.lobby

import play.api.libs.json.Json

import oyun.api.Context
import oyun.app.mashup.Preload.Homepage
import oyun.app.templating.Environment._
import oyun.app.ui.ScalatagsTemplate._
import oyun.common.String.html.safeJsonValue

import controllers.routes

object home {

  def apply(homepage: Homepage)(implicit ctx: Context) = {
    import homepage._
    views.html.base.layout(
      title = "",
      fullTitle = Some {
        s"oyunkeyf.net • ${trans.freeOnlinePoker.txt()}"
      },
      moreJs = frag(
        jsAt(s"compiled/oyunkeyf.lobby${isProd ?? (".min")}.js", defer = true),
        embedJsUnsafe(
          s"""oyunkeyf=window.oyunkeyf||{};oyunkeyf_lobby=${safeJsonValue(
Json.obj(
"data" -> data,
"i18n" -> i18nJsObject(translations)
)
)}"""
        )
      ),
      moreCss = cssTag("lobby"),
    ) {

      main(
        cls := List("lobby" -> true)
      )(
        div(cls := "lobby__table")(
          div(cls := "lobby__start")(
            a(href := routes.Lobby.home,
              cls := List("button button-metal" -> true),
              trans.createAGame())
          ),
          div(cls := "lobby__counters")(
            a(id := "nb_connected_players", href := routes.User.list.toString)(
              trans.nbPlayers(nbPlaceholder)
            ),
            a(id := "nb_games_in_play", href := routes.Lobby.home)(
              trans.nbGamesInPlay(nbPlaceholder)
            ),
          )
        ),
        div(cls := "lobby__side")(

        ),
        div(cls := "lobby__about")(
          a(href := "/about")(trans.aboutX("Oyunkeyf")),
          a(href := "/faq")("FAQ"),
          a(href := "/contact")(trans.contact()),
          a(href := "/mobile")(trans.mobileApp()),
          a(href := routes.Page.source)(trans.sourceCode())
        )
      )
    }
  }

  private val translations = List(
    trans.nbGamesInPlay,
    trans.lobby,
    trans.anonymous
  )

  private val nbPlaceholder = strong("--")
}
