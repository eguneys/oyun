package controllers

import oyun.app._

final class Main(
  env: Env,
  assetsC: ExternalAssets) extends OyunController(env) {

  def devAsset(v: String, path: String, file: String) = assetsC.at(path, file)
  
}
