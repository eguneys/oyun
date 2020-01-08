package oyun.masa

import oyun.game.{ Side, NbSeats }

final private class MasaScheduler(
  api: MasaApi,
  masaRepo: MasaRepo) {

  import Masa._
  import Side._
  import NbSeats._
  import Schedule.Plan

  private[masa] def all: List[Plan] = {
    List(
      Micro1 -> 3,
      Micro2 -> 2,
      Micro3 -> 2,
      Micro4 -> 2,
      Mini1 -> 3,
      Mini2 -> 2,
      Mini5 -> 2,
      Mini10 -> 3
    ) flatMap {
      case (stakes, nb) =>
        List.fill(nb)(Schedule(Five, stakes).plan) ++
        List.fill(nb - 1)(Schedule(Nine, stakes).plan)
    }
  }

  private[masa] def scheduleNow() {
    val newMasas = all map { _.build } 
    newMasas foreach api.create
  }
  
}
