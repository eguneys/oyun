package oyun.db

import com.typesafe.config.Config
import play.api.{ ConfigLoader, Configuration }
import reactivemongo.api._

final class Env(
  appConfig: Configuration
)(implicit ec: scala.concurrent.ExecutionContext) {

  private val driver = new AsyncDriver(appConfig.get[Config]("mongodb").some)

  def blockingDb(name: String, uri: MongoConnection.ParsedURI) = new Db(
    name = name,
    uri = uri,
    driver = driver
  )
  
}

object DbConfig {

  implicit val uriLoader = ConfigLoader { c => k =>
    MongoConnection.parseURI(c.getString(k)).get
  }

}
