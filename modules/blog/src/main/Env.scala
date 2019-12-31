package oyun.blog

import com.softwaremill.macwire._
import io.methvin.play.autoconfig._
import play.api.Configuration

private class BlogConfig(
  @ConfigName("prismic.api_url") val apiUrl: String,
  val collection: String
)

@Module
final class Env(
  appConfig: Configuration
)(implicit ec: scala.concurrent.ExecutionContext,
  ws: play.api.libs.ws.WSClient) {

  private val config = appConfig.get[BlogConfig]("blog")(AutoConfig.loader)

  lazy val api = wire[BlogApi]

}
