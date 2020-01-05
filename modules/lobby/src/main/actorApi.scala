package oyun.lobby
package actorApi

import oyun.masa.Masa

import oyun.socket.Socket.{ Sri }

private[lobby] case class JoinMasa(sri: Sri, masa: Masa, joinSide: Masa.Side)
private[lobby] case class BiteMasa(masaId: Masa.ID, sri: Sri, user: Option[LobbyUser])
private[lobby] case class LeaveBatch(sris: Iterable[Sri])
private[lobby] case object LeaveAll
