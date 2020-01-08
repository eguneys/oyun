package oyun.game

case class Schedule(
  nbSeats: NbSeats,
  stakes: Masa.Stakes
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
