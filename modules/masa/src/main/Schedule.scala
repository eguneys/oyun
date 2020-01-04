package oyun.masa

import Masa._

case class Schedule(
  nbSeats: NbSeats,
  stakes: Stakes
) {

  def plan = Schedule.Plan(this)

}

object Schedule {

  case class Plan(schedule: Schedule) {
    def build: Masa = {
      val m = Masa.scheduleAs(schedule)
      m
    }
  }
  
}
