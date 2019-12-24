package oyun.base

import ornicar.scalalib.Zero

final class PimpedBoolean(private val self: Boolean) extends AnyVal {

  def ??[A](a: => A)(implicit z: Zero[A]): A = if (self) a else z.zero

  def option[A](a: => A): Option[A] = if (self) Some(a) else None

}
