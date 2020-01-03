package oyun.socket

object Socket extends Socket {

  case class Sri(value: String) extends AnyVal with StringValue

  
}

private[socket] trait Socket {

}
