package oyun

import scalaz.Monoid
import scala.concurrent.ExecutionContext

trait PackageObject extends Oyunisms {

  def nowNanos: Long  = System.nanoTime()
  def nowMillis: Long = System.currentTimeMillis()
  def nowCentis: Long = nowMillis / 10
  def nowTenths: Long = nowMillis / 100
  def nowSeconds: Int = (nowMillis / 1000).toInt

  implicit def fuMonoid[A: Monoid](implicit ec: ExecutionContext): Monoid[Fu[A]] =
    Monoid.instance(
      (x, y) =>
      x zip y map {
        case (a, b) => a ⊹ b
      },
      fuccess(∅[A])
    )

}
