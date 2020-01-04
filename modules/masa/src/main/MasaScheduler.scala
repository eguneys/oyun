package oyun.masa

final private class MasaScheduler(
  api: MasaApi,
  masaRepo: MasaRepo) {

  import Masa._
  import Schedule.Plan

  private[masa] def all: List[Plan] = {
    List(
      Micro1 -> 3,
      Micro2 -> 3,
      Micro3 -> 3,
      Micro4 -> 3,
      Mini1 -> 3,
      Mini2 -> 3,
      Mini5 -> 3,
      Mini10 -> 3
    ) flatMap {
      case (stakes, nb) =>
        List.fill(nb)(Schedule(Five, stakes).plan) ++
        List.fill(nb)(Schedule(Nine, stakes).plan)
    }
  }

  private[masa] def scheduleNow() {
    val newMasas = all map { _.build } 
    newMasas foreach api.create
  }
  
}
