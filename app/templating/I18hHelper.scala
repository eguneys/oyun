package oyun.app
package templating

import play.api.libs.json.JsObject
import play.api.i18n.Lang

import oyun.app.ui.ScalatagsTemplate._
import oyun.i18n.{ I18nDb, I18nKey, JsDump, Translator }
import oyun.user.UserContext

trait I18nHelper extends HasEnv {

  implicit def ctxLang(implicit ctx: UserContext): Lang = ctx.lang

  def transKey(key: String, db: I18nDb.Ref, args: Seq[Any] = Nil)(implicit lang: Lang): Frag =
    Translator.frag.literal(key, db, args, lang)

  def i18nJsObject(keys: Seq[I18nKey])(implicit lang: Lang): JsObject =
    JsDump.keysToObject(keys, I18nDb.Site, lang)

}
