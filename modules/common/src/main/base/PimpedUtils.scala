package oyun.base

final class PimpedOption[A](private val self: Option[A]) extends AnyVal {


  def |(a: => A): A = self getOrElse a

  def has(a: A) = self contains a

}
