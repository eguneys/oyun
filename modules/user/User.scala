package oyun.user

case class User(
  id: String,
  username: String,
  lang: Option[String]
) {


}

object User {

  type ID = String



}
