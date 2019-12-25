package oyun.app

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

  lazy val httpFilters = Seq()

  implicit def system = actorSystem
  implicit def ws = wsClient

  implicit def mimeTypes = fileMimeTypes
  lazy val devAssetsController = wire[ExternalAssets]

  lazy val boot: oyun.app.EnvBoot = wire[oyun.app.EnvBoot]
  lazy val env: oyun.app.Env = boot.env

  lazy val lobby: Lobby = wire[Lobby]
  lazy val main: Main = wire[Main]

  val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

}
