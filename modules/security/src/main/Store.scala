package oyun.security

import org.joda.time.DateTime
import play.api.mvc.RequestHeader

import oyun.db.dsl._
import oyun.user.User

final class Store(
  val coll: Coll
)(implicit ec: scala.concurrent.ExecutionContext) {

  def save(
    sessionId: String,
    userId: User.ID,
    req: RequestHeader,
    up: Boolean): Funit =
    coll.insert
      .one($doc(
        "_id" -> sessionId,
        "user" -> userId,
        "date" -> DateTime.now,
        "up" -> up
      )).void

  def userId(sessionId: String): Fu[Option[User.ID]] = coll.primitiveOne[User.ID]($doc("_id" -> sessionId, "up" -> true), "user")

}
