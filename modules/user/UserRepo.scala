package oyun.user

final class UserRepo(
)(implicit ec: scala.concurrent.ExecutionContext) {

  import User.ID

  def byId(id: ID): Fu[Option[User]] = ???

}
