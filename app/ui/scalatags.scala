package oyun.app
package ui

import scalatags.Text.all._
import scalatags.text.Builder
import scalatags.Text.{ Aggregate, Cap }

trait ScalatagsAttrs {

  val dataAssetUrl = attr("data-asset-url")
  val dataAssetVersion = attr("data-asset-version")
  val dataDev = attr("data-dev")
  val dataTag = attr("data-tag")
  val dataIcon = attr("data-icon")
  val dataHref = attr("data-href")
  val deferAttr = attr("defer").empty

}

trait ScalatagsSnippets extends Cap {

}

trait ScalatagsBundle
  extends Cap
    with Attrs
    with scalatags.text.Tags
    with Aggregate

trait ScalatagsPrefix {

  object st extends Cap with Attrs with scalatags.text.Tags {
    val group = tag("group")
    val headTitle = tag("title")
    val nav = tag("nav")
    val section = tag("section")
    val article = tag("article")
    val aside = tag("aside")

  }

}

trait ScalatagsTemplate
    extends Styles
    with ScalatagsBundle
    with ScalatagsAttrs
    with ScalatagsExtensions
    with ScalatagsSnippets
    with ScalatagsPrefix {

  val trans = oyun.i18n.I18nKeys
  def main = scalatags.Text.tags2.main


  implicit val playCallAttr = genericAttr[play.api.mvc.Call]

}

object ScalatagsTemplate extends ScalatagsTemplate

trait ScalatagsExtensions {


  implicit val optionStringAttr = new AttrValue[Option[String]] {
    def apply(t: scalatags.text.Builder, a: Attr, v: Option[String]): Unit = {
      v foreach { s =>
        t.setAttr(a.name, scalatags.text.Builder.GenericAttrValueSource(s))
      }
    }
  }


  implicit val classesAttr = new AttrValue[List[(String, Boolean)]] {
    def apply(t: scalatags.text.Builder, a: Attr, m: List[(String, Boolean)]): Unit = {
      val cls = m collect { case (s, true) => s } mkString " "
      if (cls.nonEmpty) t.setAttr(a.name, scalatags.text.Builder.GenericAttrValueSource(cls))
    }
  }

  val emptyFrag: Frag = new RawFrag("")


}
