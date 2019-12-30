package oyun

import scalaz.Monoid
import scala.concurrent.ExecutionContext

trait PackageObject extends Oyunisms {

  def nowNanos: Long = System.nanoTime()

  implicit def fuMonoid[A: Monoid](implicit ec: ExecutionContext): Monoid[Fu[A]] =
    Monoid.instance(
      (x, y) =>
      x zip y map {
        case (a, b) => a ⊹ b
      },
      fuccess(∅[A])
    )

}
