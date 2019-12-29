package oyun.user

import reactivemongo.api.bson._

import oyun.db.dsl._
import oyun.common.NormalizedEmailAddress
import oyun.user.User.{ ClearPassword, BSONFields => F }

final class Authenticator()
(implicit ec: scala.concurrent.ExecutionContext) {

  import Authenticator._

  def authenticateById(id: User.ID, password: ClearPassword): Fu[Option[User]] = ???

  def authenticateByEmail(email: NormalizedEmailAddress,
    password: ClearPassword): Fu[Option[User]] = ???


  def loginCandidateById(id: User.ID): Fu[Option[User.LoginCandidate]] =
    loginCandidate($id(id))

  def loginCandidateByEmail(email: NormalizedEmailAddress): Fu[Option[User.LoginCandidate]] =
    loginCandidate($doc(F.email -> email))


  private def loginCandidate(select: Bdoc): Fu[Option[User.LoginCandidate]] =
    ???

}

object Authenticator {

}
