package oyun.masa
package actorApi

import scala.concurrent.Promise

import poker.{ Side }
import oyun.user.User
import oyun.socket.Socket.SocketVersion

case class GetSocketStatus(promise: Promise[SocketStatus])
case class SocketStatus(
  version: SocketVersion
)

package masa {

  case class Buyin(userId: User.ID, side: Side)
  case class SitoutNext(side: Side, value: Boolean)

  case object MaybeDeal

  
}
