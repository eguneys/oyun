package oyun.common

import com.softwaremill.macwire._
import play.api.Configuration
import play.api.libs.ws.WSClient

import config._

final class Env(
  appConfig: Configuration,
  ws: WSClient)(implicit ec: scala.concurrent.ExecutionContext) {


  val netConfig = appConfig.get[NetConfig]("net")
  def netDomain = netConfig.domain

}
