package oyun.base

import OyunTypes._
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext => EC, Future, Await }

final class PimpedFuture[A](private val fua: Fu[A]) extends AnyVal {

  @inline def dmap[B](f: A => B): Fu[B] = fua.map(f)(EC.parasitic)
  @inline def dforeach[B](f: A => Unit): Unit = fua.foreach(f)(EC.parasitic)

  def >>-(sideEffect: => Unit)(implicit ec: EC): Fu[A] = fua andThen {
    case _ => sideEffect
  }

  def >>[B](fub: => Fu[B])(implicit ec: EC): Fu[B] = fua flatMap { _ =>
    fub
  }

  @inline def void: Fu[Unit] = dmap { _ => 
    ()
  }

  @inline def inject[B](b: => B): Fu[B] = dmap { _ =>
    b
  }

  def await(duration: FiniteDuration, name: String): A =
    Await.result(fua, duration)

}

final class PimpedFutureBoolean(private val fua: Fu[Boolean]) extends AnyVal {

  @inline def unary_! = fua.map { !_ }(EC.parasitic)

}

final class PimpedFutureOption[A](private val fua: Future[Option[A]]) extends AnyVal {

  def orFail(msg: => String)(implicit ec: EC): Fu[A] = fua flatMap {
    _.fold[Fu[A]](fufail(msg))(fuccess(_))
  }

  def map2[B](f: A => B)(implicit ec: EC): Fu[Option[B]] = fua.map(_ map f)
  def dmap2[B](f: A => B): Fu[Option[B]] = fua.map(_ map f)(EC.parasitic)
  
}
