package oyun.base

import scala.util.Try

import ornicar.scalalib.Zero

final class PimpedOption[A](private val self: Option[A]) extends AnyVal {


  def |(a: => A): A = self getOrElse a

  def unary_~(implicit z: Zero[A]): A = self getOrElse z.zero


  def toTryWith(err: => Exception): Try[A] =
    self.fold[Try[A]](scala.util.Failure(err))(scala.util.Success.apply)

  def toTry(err: => String): Try[A] = toTryWith(oyun.base.OyunException(err))

  def err(message: => String): A = self.getOrElse(sys.error(message))

  def has(a: A) = self contains a

}

final class PimpedString(private val s: String) extends AnyVal {

  def replaceIf(t: Char, r: Char): String =
    if (s.indexOf(t) >= 0) s.replace(t, r) else s

  def replaceIf(t: Char, r: CharSequence): String =
    if (s.indexOf(t) >= 0) s.replace(String.valueOf(t), r) else s

  def replaceIf(t: CharSequence, r: CharSequence): String =
    if (s.contains(t)) s.replace(t, r) else s
  
}
