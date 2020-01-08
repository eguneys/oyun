package oyun.app

import akka.actor._
import com.softwaremill.macwire._
import play.api.libs.ws.WSClient
import play.api.mvc.{ ControllerComponents, SessionCookieBaker }
import play.api.{ Configuration, Environment, Mode }

import scala.concurrent.{ ExecutionContext }

import oyun.common.config._
import oyun.user.User

final class Env(
  val config: Configuration,
  val common: oyun.common.Env,
  val api: oyun.api.Env,
  val user: oyun.user.Env,
  val security: oyun.security.Env,
  val blog: oyun.blog.Env,
  val memo: oyun.memo.Env,
  val socket: oyun.socket.Env,
  val lobby: oyun.lobby.Env,
  val masa: oyun.masa.Env,
  val round: oyun.round.Env,
  val oyunCookie: oyun.common.OyunCookie,
  val controllerComponents: ControllerComponents
)(implicit val system: ActorSystem, val executionContext: ExecutionContext, val mode: play.api.Mode) {

  val isProd = mode == Mode.Prod
  val isDev = mode == Mode.Dev

  def net = common.netConfig

  def lobbyApi = api.lobbyApi

  lazy val preloader = wire[mashup.Preload]
  
}

final class EnvBoot(
  config: Configuration,
  environment: Environment,
  controllerComponents: ControllerComponents,
  cookieBaker: SessionCookieBaker,
  shutdown: CoordinatedShutdown
)(implicit ec: ExecutionContext, system: ActorSystem, ws: WSClient) {


  implicit def mode = environment.mode
  def baseUrl = common.netConfig.baseUrl
  def net = common.netConfig

  import reactivemongo.api.MongoConnection.ParsedURI
  import oyun.db.DbConfig.uriLoader
  lazy val mainDb: oyun.db.Db = mongo.blockingDb("main", config.get[ParsedURI]("mongodb.uri"))

  lazy val common: oyun.common.Env     = wire[oyun.common.Env]
  lazy val memo: oyun.memo.Env         = wire[oyun.memo.Env]
  lazy val mongo: oyun.db.Env          = wire[oyun.db.Env]
  lazy val user: oyun.user.Env         = wire[oyun.user.Env]
  lazy val security: oyun.security.Env = wire[oyun.security.Env]
  lazy val blog: oyun.blog.Env         = wire[oyun.blog.Env]
  lazy val socket: oyun.socket.Env     = wire[oyun.socket.Env]
  lazy val lobby: oyun.lobby.Env       = wire[oyun.lobby.Env]
  lazy val game: oyun.game.Env         = wire[oyun.game.Env]
  lazy val masa: oyun.masa.Env         = wire[oyun.masa.Env]
  lazy val round: oyun.round.Env       = wire[oyun.round.Env]
  lazy val api: oyun.api.Env           = wire[oyun.api.Env]
  lazy val oyunCookie                  = wire[oyun.common.OyunCookie]

  val env: oyun.app.Env = {
    val c = wire[oyun.app.Env]
    oyun.log("boot").info(s"Loaded oyun modules in duration")
    c
  }

  templating.Environment setEnv env


}
