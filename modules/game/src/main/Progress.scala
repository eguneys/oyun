package oyun.game

case class Progress(origin: Masa, masa: Masa, events: List[Event] = Nil) {

  def ++(es: List[Event]) = copy(events = events ::: es)

}

object Progress {

  def apply(masa: Masa): Progress =
    new Progress(masa, masa)
}
