package oyun.masa

import akka.actor._
import com.softwaremill.macwire._
// import io.methvin.play.autoconfig._
// import play.api.Configuration

import actorApi. { GetSocketStatus, SocketStatus }
// import oyun.common.config._
import oyun.game.{ Masa }
// import oyun.user.User

private class MasaConfig(
)


@Module
final class Env(
  // appConfig: Configuration,
  // db: oyun.db.Db,
  remoteSocketApi: oyun.socket.RemoteSocket,
  lightUserApi: oyun.user.LightUserApi,
  userRepo: oyun.user.UserRepo,
  masaRepo: oyun.game.MasaRepo
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {


  // private val config = appConfig.get[MasaConfig]("masa")(AutoConfig.loader)

  private lazy val masaScheduler = wire[MasaScheduler]
  masaScheduler.scheduleNow()

  lazy val getSocketStatus = (masa: Masa) => masaSocket.masas.ask[SocketStatus](masa.id)(GetSocketStatus)

  lazy val api: MasaApi = wire[MasaApi]

  lazy val jsonView = wire[JsonView]

  lazy val apiJsonView = wire[ApiJsonView]

  private lazy val proxyDependencies = new MasaProxy.Dependencies(masaRepo)
  private lazy val roundDependencies = wire[MasaDuct.Dependencies]

  lazy val masaSocket: MasaSocket = wire[MasaSocket]

  lazy val proxyRepo: MasaProxyRepo = wire[MasaProxyRepo]

  private lazy val finisher: Finisher = wire[Finisher]
  private lazy val sitter: Sitter = wire[Sitter]
  private lazy val dealer: Dealer = wire[Dealer]
  private lazy val player: Player = wire[Player]
}
