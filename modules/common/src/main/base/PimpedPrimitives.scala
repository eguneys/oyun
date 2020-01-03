package oyun.base

import java.lang.Math.{ max, min }

import ornicar.scalalib.Zero

final class PimpedBoolean(private val self: Boolean) extends AnyVal {

  def ??[A](a: => A)(implicit z: Zero[A]): A = if (self) a else z.zero

  def option[A](a: => A): Option[A] = if (self) Some(a) else None

}


final class PimpedInt(private val self: Int) extends AnyVal {

  def atLeast(bottomValue: Int): Int = max(self, bottomValue)

}
