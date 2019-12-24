package oyun.security

import oyun.user.User

final class Store(
)(implicit ec: scala.concurrent.ExecutionContext) {

  def userId(sessionId: String): Fu[Option[User.ID]] = fufail("not implemented")

}
