package oyun

package object masa extends PackageObject {

  private[masa] val logger = oyun.log("masa")

  private[masa] type Rounds = List[Round]

  private[masa] type Masas = List[Masa]

  private[masa] type Players = List[Player]
  
}
