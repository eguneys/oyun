package oyun.api

import akka.actor._
import com.softwaremill.macwire._
import play.api.{ Configuration, Mode }

import oyun.common.config._

final class Env(
  appConfig: Configuration,
  net: NetConfig
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {



}
