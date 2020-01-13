package oyun.user

import play.api.libs.json._

final class JsonView() {


  def apply(u: User): JsObject =
    Json.obj(
      "id" -> u.id,
      "username" -> u.username,
      "avatar" -> u.avatar.link
    )

}
