package oyun.lobby
package actorApi

import oyun.socket.Socket.{ Sri }

private[lobby] case class LeaveBatch(sris: Iterable[Sri])
private[lobby] case object LeaveAll
