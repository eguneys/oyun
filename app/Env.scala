package oyun.app

import akka.actor._
import com.softwaremill.macwire._
import play.api.libs.ws.WSClient
import scala.concurrent.{ ExecutionContext }

final class Env(
)(implicit val system: ActorSystem, val executionContext: ExecutionContext) {


  
}

final class EnvBoot(
)(implicit ec: ExecutionContext, system: ActorSystem, ws: WSClient) {


  val env: oyun.app.Env = {
    val c = wire[oyun.app.Env]

    c
  }


}
