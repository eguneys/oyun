package oyun.user

import akka.actor._
import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration
import play.api.libs.ws.WSClient

import oyun.common.config._

@Module
final class Env(
  appConfig: Configuration
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem, ws: WSClient) {


  val repo = new UserRepo()
  
}
