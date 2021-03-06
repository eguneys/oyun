package oyun.app
package templating

import play.api.mvc.RequestHeader

import oyun.api.Context
import oyun.app.ui.ScalatagsTemplate._
import oyun.common.{ AssetVersion }

trait AssetHelper { self: I18nHelper =>

  def isProd: Boolean

  lazy val assetDomain = env.net.assetDomain
  lazy val socketDomain = env.net.socketDomain

  lazy val assetBaseUrl = s"//$assetDomain"

  def assetVersion = AssetVersion.current

  def assetUrl(path: String): String = s"$assetBaseUrl/assets/_$assetVersion/$path"

  def staticUrl(path: String) = s"$assetBaseUrl/assets/$path"

  def cssTag(name: String)(implicit ctx: Context): Frag =
    cssTagWithTheme(name, "dark")

  def cssTagWithTheme(name: String, theme: String): Frag =
    cssAt(s"css/$name.$theme.${if (isProd) "min" else "dev"}.css")

  private def cssAt(path: String): Frag =
    link(href := assetUrl(path), tpe := "text/css", rel := "stylesheet")


  def jsTag(name: String, defer: Boolean = false): Frag =
    jsAt("javascripts/" + name, defer = defer)

  def jsAt(path: String, defer: Boolean = false): Frag = script(
    defer option deferAttr,
    src := assetUrl(path))


  def masaTag = jsAt(s"compiled/oyunkeyf.masa${isProd ?? (".min")}.js", defer = true)

  def embedJsUnsafe(js: String): Frag = raw {
    s"""<script>$js</script>"""
  }
  
}
