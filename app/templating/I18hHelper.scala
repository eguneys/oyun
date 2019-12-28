package oyun.app
package templating

import play.api.i18n.Lang

import oyun.user.UserContext

trait I18nHelper extends HasEnv {

  implicit def ctxLang(implicit ctx: UserContext): Lang = ctx.lang

}
