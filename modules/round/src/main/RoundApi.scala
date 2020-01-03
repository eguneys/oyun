package oyun.round

import akka.actor.{ ActorSystem }

final class RoundApi(
  roundRepo: RoundRepo
)(
  implicit ec: scala.concurrent.ExecutionContext,
  system: ActorSystem) {


  def fetchVisibleRounds: Fu[Rounds] =
    ???

  
}
