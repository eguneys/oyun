package oyun.common

import play.api.libs.json.{ Json, OWrites }

case class LightUser(
  id: String,
  name: String) {

}

object LightUser {

  private type UserID = String


  implicit val lightUserWrites = OWrites[LightUser] { u =>
    Json
      .obj(
        "id"   -> u.id,
        "name" -> u.name
      )
  }


  final class Getter(f: UserID => Fu[Option[LightUser]]) extends (UserID => Fu[Option[LightUser]]) {
    def apply(u: UserID) = f(u)
  }

}
