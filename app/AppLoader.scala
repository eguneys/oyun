package oyun.app

import akka.actor.CoordinatedShutdown
import com.softwaremill.macwire._
import play.api._
import play.api.mvc._
import play.api.routing.Router
import router.Routes

final class AppLoader extends ApplicationLoader {
  def load(ctx: ApplicationLoader.Context): Application = new OyunComponents(ctx).application
}

final class OyunComponents(ctx: ApplicationLoader.Context)
    extends BuiltInComponentsFromContext(ctx)
    with _root_.controllers.AssetsComponents
    with play.api.libs.ws.ahc.AhcWSComponents {

  LoggerConfigurator(ctx.environment.classLoader).foreach {
    _.configure(ctx.environment, ctx.initialConfiguration, Map.empty)
  }

  import _root_.controllers._

  def cookieBaker = new LegacySessionCookieBaker(httpConfiguration.session, cookieSigner)


  lazy val httpFilters = Seq()

  implicit def system = actorSystem
  implicit def ws = wsClient

  implicit def mimeTypes = fileMimeTypes
  lazy val devAssetsController = wire[ExternalAssets]

  lazy val shutdown = CoordinatedShutdown(system)

  lazy val boot: oyun.app.EnvBoot = wire[oyun.app.EnvBoot]
  lazy val env: oyun.app.Env = boot.env

  lazy val auth: Auth = wire[Auth]
  lazy val lobby: Lobby = wire[Lobby]
  lazy val user: User = wire[User]
  lazy val dasher: Dasher = wire[Dasher]
  lazy val page: Page = wire[Page]
  lazy val main: Main = wire[Main]
  lazy val blog: Blog = wire[Blog]
  lazy val prismic: Prismic = wire[Prismic]

  val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

}
