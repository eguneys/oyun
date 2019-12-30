package oyun.user

import oyun.db.dsl._

final class UserRepo(
  val coll: Coll
)(implicit ec: scala.concurrent.ExecutionContext) {

  import User.ID

  def byId(id: ID): Fu[Option[User]] = ???

}
