package oyun.masa
package actorApi

import scala.concurrent.Promise

import oyun.game.{ Side }
import oyun.user.User
import oyun.socket.Socket.SocketVersion

case class GetSocketStatus(promise: Promise[SocketStatus])
case class SocketStatus(
  version: SocketVersion
)

package masa {

  case class Buyin(userId: User.ID, side: Side)
  
}
