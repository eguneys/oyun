package controllers

import play.api.libs.json._

import oyun.api.Context
import oyun.app._
import oyun.common.LightUser.lightUserWrites
import oyun.i18n.{ enLang, I18nKeys, I18nLangPicker }


final class Dasher(env: Env) extends OyunController(env) {

  private val translationsBase = List(
  )

  private val translationsAnon = List(
    I18nKeys.signIn,
    I18nKeys.signUp
  ) ::: translationsBase

  private val translationsAuth = List(
    I18nKeys.profile,
    I18nKeys.logOut
  ) ::: translationsBase

  private def translations(implicit ctx: Context) =
    oyun.i18n.JsDump.keysToObject(
      if (ctx.isAnon) translationsAnon else translationsAuth,
      oyun.i18n.I18nDb.Site,
      ctx.lang
    )


  def get = Open { implicit ctx =>
    negotiate(
      html = notFound,
      api = _ =>
      ctx.me.??(_ => funit) map { _ =>
        Ok {
          Json.obj(
            "user" -> ctx.me.map(_.light),
            "lang" -> Json.obj(
              "current" -> ctx.lang.code
            ),
            "background" -> Json.obj(
              "current" -> ctx.currentBg
            ),
            "i18n" -> translations
          )
        }
      })
  }
  
}
