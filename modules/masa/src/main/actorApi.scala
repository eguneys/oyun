package oyun.masa
package actorApi

import scala.concurrent.Promise

import poker.{ Side }
import poker.format.Uci
import oyun.user.User
import oyun.socket.Socket.SocketVersion

case class GetSocketStatus(promise: Promise[SocketStatus])
case class SocketStatus(
  version: SocketVersion
)

package masa {

  case class HumanPlay(
    userId: User.ID,
    uci: Uci)

  case class Buyin(userId: User.ID, side: Side)
  case class SitoutNext(side: Side, value: Boolean)

  case object MaybeDeal

  
}
