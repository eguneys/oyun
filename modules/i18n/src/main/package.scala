package oyun

import play.api.i18n.Lang

package object i18n extends PackageObject {

  type Count = Int
  type MessageKey = String

  private[i18n] type MessageMap = java.util.Map[MessageKey, Translation]
  private[i18n] type Messages = Map[Lang, MessageMap]

  private[i18n] def logger = oyun.log("i18n")

  val enLang = Lang("en", "GB")
  val defaultLang = enLang
}
