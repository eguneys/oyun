package oyun

package object round extends PackageObject {

  private[round] val logger = oyun.log("round")

  private[round] type Rounds = List[Round]

  private[round] type Masas = List[Masa]

  private[round] type Players = List[Player]
  
}
