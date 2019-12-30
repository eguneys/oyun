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
