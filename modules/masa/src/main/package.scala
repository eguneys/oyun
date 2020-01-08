package oyun

import oyun.game.Event

package object masa extends PackageObject {

  private[masa] val logger = oyun.log("masa")

  private[masa] type Events = List[Event]
}

package masa {

  sealed private[masa] trait BenignError extends oyun.base.OyunException
  private[masa] case class ClientError(message: String) extends BenignError
  
}
