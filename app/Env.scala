package oyun.app

import akka.actor._
import com.softwaremill.macwire._
import play.api.libs.ws.WSClient
import play.api.mvc.{ ControllerComponents }
import play.api.{ Configuration, Environment, Mode }

import scala.concurrent.{ ExecutionContext }

final class Env(
  val config: Configuration,
  val mode: Mode,
  val common: oyun.common.Env,
  val controllerComponents: ControllerComponents
)(implicit val system: ActorSystem, val executionContext: ExecutionContext) {

  val isProd = mode == Mode.Prod
  val isDev = mode == Mode.Dev

  def net = common.netConfig
  
}

final class EnvBoot(
  config: Configuration,
  environment: Environment,
  controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext, system: ActorSystem, ws: WSClient) {


  def mode = environment.mode


  lazy val common: oyun.common.Env = wire[oyun.common.Env]

  val env: oyun.app.Env = {
    val c = wire[oyun.app.Env]

    c
  }


}
