package oyun.game

sealed trait Stakes {

  val blinds: Float

  def buyIn = minBuyIn

  def maxBuyIn = blinds * 20
  def minBuyIn = blinds * 10

}

case object Stakes {

  case object Micro1 extends Stakes {
    val blinds = 0.05f
  }
  case object Micro2 extends Stakes {
    val blinds = 0.1f
  }
  case object Micro3 extends Stakes {
    val blinds = 0.25f
  }
  case object Micro4 extends Stakes {
    val blinds = 0.5f
  }
  case object Mini1 extends Stakes {
    val blinds = 1.0f
  }
  case object Mini2 extends Stakes {
    val blinds = 2.0f
  }
  case object Mini5 extends Stakes {
    val blinds = 5.0f
  }
  case object Mini10 extends Stakes {
    val blinds = 10.0f
  }

}
