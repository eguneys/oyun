package oyun.user

import reactivemongo.api.bson._
import scala.concurrent.duration._
import scala.util.Success

import oyun.common.LightUser
import oyun.db.dsl._
import oyun.memo.{ CacheApi, Syncache }
import User.{ BSONFields => F }

final class LightUserApi(
  repo: UserRepo,
  cacheApi: CacheApi
)(implicit ec: scala.concurrent.ExecutionContext) {

  import LightUserApi._

  val async = new LightUser.Getter(cache.async)


  private val cache = cacheApi.sync[User.ID, Option[LightUser]](
    name = "user.light",
    initialCapacity = 131072,
    compute = id => repo.coll.find($id(id), projection).one[LightUser],
    default = id => LightUser(id, id).some,
    strategy = Syncache.WaitAfterUptime(8 millis),
    expireAfter = Syncache.ExpireAfterWrite(20 minutes)
  )
}

private object LightUserApi {


  implicit val lightUserBSONReader = new BSONDocumentReader[LightUser] {
    def readDocument(doc: BSONDocument) =
      Success(
        LightUser(
          id = doc.string(F.id) err "LightUser id missing",
          name = doc.string(F.username) err "LightUser username missing"
        )
      )
  }

  val projection = $doc(F.username -> true).some

}
