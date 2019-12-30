package oyun.user

import reactivemongo.api.bson._

import oyun.db.dsl._
import oyun.common.NormalizedEmailAddress
import oyun.user.User.{ ClearPassword, BSONFields => F }

final class Authenticator(
  passHasher: PasswordHasher,
  userRepo: UserRepo
)
(implicit ec: scala.concurrent.ExecutionContext) {

  import Authenticator._

  def passEnc(p: ClearPassword): HashedPassword = passHasher.hash(p)

  def compare(auth: AuthData, p: ClearPassword): Boolean = {
    val newP = p
    passHasher.check(auth.bpass, newP)
  }

  def authenticateById(id: User.ID, password: ClearPassword): Fu[Option[User]] = ???

  def authenticateByEmail(email: NormalizedEmailAddress,
    password: ClearPassword): Fu[Option[User]] = ???


  def loginCandidateById(id: User.ID): Fu[Option[User.LoginCandidate]] =
    loginCandidate($id(id))

  def loginCandidateByEmail(email: NormalizedEmailAddress): Fu[Option[User.LoginCandidate]] =
    loginCandidate($doc(F.email -> email))


  private def authWithBenefits(auth: AuthData)(p: ClearPassword): Boolean = {
    val res = compare(auth, p)
    res
  }

  private def loginCandidate(select: Bdoc): Fu[Option[User.LoginCandidate]] =
    userRepo.coll.one[AuthData](select, authProjection)(AuthDataBSONHandler) zip userRepo.coll.one[User](select) map {
      case (Some(authData), Some(user)) if user.enabled =>
        User.LoginCandidate(user, authWithBenefits(authData)).some
      case _ => none
    }

}

object Authenticator {

  case class AuthData(
    _id: User.ID,
    bpass: HashedPassword)

  val authProjection = $doc(
    F.bpass -> true
  )

  implicit private[user] val HashedPasswordBsonHandler = quickHandler[HashedPassword](
    { case v: BSONBinary => HashedPassword(v.byteArray) },
    v => BSONBinary(v.bytes, Subtype.GenericBinarySubtype)
  )

  implicit val AuthDataBSONHandler = Macros.handler[AuthData]

}
