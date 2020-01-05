package oyun.socket

import play.api.libs.json._

object Socket extends Socket {

  case class Sri(value: String) extends AnyVal with StringValue
  
}

private[socket] trait Socket {

  def makeMessage[A](t: String, data: A)(implicit writes: Writes[A]): JsObject =
    JsObject(new Map.Map2("t", JsString(t), "d", writes.writes(data)))

  def makeMessage(t: String): JsObject = JsObject(new Map.Map1("t", JsString(t)))

}
