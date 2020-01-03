package oyun.memo

import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration

import oyun.common.config._

final class MemoConfig(
  @ConfigName("collection.cache") val cacheColl: CollName,
  @ConfigName("collection.config") val configColl: CollName,
)

@Module
final class Env(
  appConfig: Configuration,
  mode: play.api.Mode,
  db: oyun.db.Db
)(implicit ec: scala.concurrent.ExecutionContext, system: akka.actor.ActorSystem) {

  private val config = appConfig.get[MemoConfig]("memo")(AutoConfig.loader)

  lazy val cacheApi = wire[CacheApi]

}
