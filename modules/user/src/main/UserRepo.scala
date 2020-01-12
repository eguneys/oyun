package oyun.user

import org.joda.time.DateTime
import reactivemongo.api._
import reactivemongo.api.bson._

import oyun.common.{ EmailAddress, NormalizedEmailAddress }
import oyun.db.BSON.BSONJodaDateTimeHandler
import oyun.db.dsl._

final class UserRepo(
  val coll: Coll
)(implicit ec: scala.concurrent.ExecutionContext) {

  import User.ID
  import User.{ BSONFields => F }

  val normalize = User normalize _

  def byId(id: ID): Fu[Option[User]] = coll.byId[User](id)

  def enabledById(id: ID): Fu[Option[User]] =
    coll.one[User](enabledSelect ++ $id(id))

  val enabledSelect = $doc(F.enabled -> true)

  def named(username: String): Fu[Option[User]] = coll.byId[User](normalize(username))

  def create(
    username: String,
    passwordHash: HashedPassword,
    email: EmailAddress): Fu[Option[User]] =
    !nameExists(username) flatMap {
      _ ?? {
        val doc = newUser(username,
          Avatar.pickRandom,
          passwordHash,
          email) ++
        ("len" -> BSONInteger(username.size))
        coll.insert.one(doc) >> named(normalize(username))
      }
    }

  def nameExists(username: String): Fu[Boolean] = idExists(normalize(username))
  def idExists(id: String): Fu[Boolean] = coll exists $id(id)


  import Authenticator._

  private def newUser(
    username: String,
    avatar: Avatar,
    passwordHash: HashedPassword,
    email: EmailAddress) = {
    val normalizedEmail = email.normalize

    $doc(
      F.id -> normalize(username),
      F.username -> username,
      F.avatar -> avatar.value,
      F.email -> normalizedEmail,
      F.bpass -> passwordHash,
      F.enabled -> true,
      F.createdAt -> DateTime.now,
      F.seenAt -> DateTime.now
    )
  }

}
