package oyun.i18n

import play.api.i18n.Lang

object I18nDb {

  sealed trait Ref
  case object Site extends Ref

  val site: Messages = oyun.i18n.db.site.Registry.load

  def apply(ref: Ref): Messages = ref match {
    case Site => site
  }

  val langs: Set[Lang] = site.keys.toSet
  
}
