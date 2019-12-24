package oyun.app

import akka.actor._
import com.softwaremill.macwire._
import play.api.libs.ws.WSClient
import play.api.mvc.{ ControllerComponents }
import play.api.{ Configuration, Environment, Mode }

import scala.concurrent.{ ExecutionContext }

import oyun.common.config._
import oyun.user.User

final class Env(
  val config: Configuration,
  val mode: Mode,
  val common: oyun.common.Env,
  val api: oyun.api.Env,
  val user: oyun.user.Env,
  val security: oyun.security.Env,
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
  def baseUrl = common.netConfig.baseUrl
  def net = common.netConfig

  lazy val common: oyun.common.Env = wire[oyun.common.Env]
  lazy val user: oyun.user.Env = wire[oyun.user.Env]
  lazy val security: oyun.security.Env = wire[oyun.security.Env]
  lazy val api: oyun.api.Env = wire[oyun.api.Env]

  val env: oyun.app.Env = {
    val c = wire[oyun.app.Env]
    oyun.log("boot").info(s"Loaded oyun modules in duration")
    c
  }

  templating.Environment setEnv env


}
