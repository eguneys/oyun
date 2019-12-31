package controllers

import oyun.app._

import views._

final class Main(
  env: Env,
  assetsC: ExternalAssets) extends OyunController(env) {

  def devAsset(v: String, path: String, file: String) = assetsC.at(path, file)

  def contact = Open { implicit ctx =>
    Ok(html.site.contact()).fuccess
  }

  def faq = Open { implicit ctx =>
    Ok(html.site.faq()).fuccess
  }

  def movedPermanently(to: String) = Action {
    MovedPermanently(to)
  }
  
}
