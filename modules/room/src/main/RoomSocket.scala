package oyun.room

import oyun.socket.RemoteSocket.{ Protocol => P, _ }

object RoomSocket {

  case class RoomId(value: String) extends AnyVal with StringValue

  object Protocol {
    object In {

      val reader: P.In.Reader = raw =>
      raw.path match {
        case _ => P.In.baseReader(raw)
      }
    }
  }

}
