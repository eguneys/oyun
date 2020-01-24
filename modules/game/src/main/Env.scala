package oyun.game

import akka.actor._
import com.softwaremill.macwire._
// import io.methvin.play.autoconfig._
import play.api.Configuration

@Module
final class Env(
//   appConfig: Configuration,
  db: oyun.db.Db
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {

  lazy val masaRepo = new MasaRepo()

}
