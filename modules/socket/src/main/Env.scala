package oyun.socket

import akka.actor._
import com.softwaremill.macwire._
import io.lettuce.core._
import play.api.Configuration

@Module
final class Env(
  appConfig: Configuration,
  shutdown: CoordinatedShutdown
)(implicit ec: scala.concurrent.ExecutionContext, akka: ActorSystem) {

  private val RedisUri = appConfig.get[String]("socket.redis.uri")

  private val redisClient = RedisClient create RedisURI.create(RedisUri)

  val remoteSocket: RemoteSocket = wire[RemoteSocket]
  
}
