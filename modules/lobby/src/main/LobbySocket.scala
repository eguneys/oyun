package oyun.lobby

import oyun.socket.RemoteSocket.{ Protocol => P, _ }

final class LobbySocket(
  remoteSocketApi: oyun.socket.RemoteSocket
)(implicit ec: scala.concurrent.ExecutionContext) {




  remoteSocketApi.subscribe("lobby-in", P.In.baseReader)(remoteSocketApi.baseHandler)
  
}
