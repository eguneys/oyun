package controllers

import oyun.app._

final class Page(
  env: Env,
  prismicC: Prismic
) extends OyunController(env) {

  val thanks = helpBookmark("thanks")
  val help = helpBookmark("help")
  val about = helpBookmark("about")

  private def helpBookmark(name: String) = Open { implicit ctx =>
    OptionOk(prismicC getBookmark name) {
      case (doc, resolver) => views.html.site.help.page(name, doc, resolver)
    }
  }


  def source = Open { implicit ctx =>
    OptionOk(prismicC getBookmark "source") {
      case (doc, resolver) => views.html.site.help.source(doc, resolver)
    }
  }
  
}
