package oyun.base

import OyunTypes._
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext => EC, Future, Await }

final class PimpedFuture[A](private val fua: Fu[A]) extends AnyVal {

  @inline def dmap[B](f: A => B): Fu[B] = fua.map(f)(EC.parasitic)
  @inline def dforeach[B](f: A => Unit): Unit = fua.foreach(f)(EC.parasitic)

  @inline def void: Fu[Unit] = dmap { _ => () }


  def await(duration: FiniteDuration, name: String): A =
    Await.result(fua, duration)

}
