package oyun.base

import akka.actor.ActorSystem
import scala.collection.BuildFrom
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext => EC, Future, Await }

import OyunTypes._

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



  def withTimeout(duration: FiniteDuration)(implicit ec: EC, system: ActorSystem): Fu[A] =
    withTimeout(duration, OyunException(s"Future timed out after $duration"))

  def withTimeout(
    duration: FiniteDuration,
    error: => Throwable
  )(implicit ec: EC, system: ActorSystem): Fu[A] = {
    Future firstCompletedOf Seq(
      fua,
      akka.pattern.after(duration, system.scheduler)(Future failed error)
    )
  }


  def addEffects(fail: Exception => Unit, succ: A => Unit)(implicit ec: EC): Fu[A] = {
    fua onComplete {
      case scala.util.Failure(e: Exception) => fail(e)
      case scala.util.Failure(e) => throw e
      case scala.util.Success(e) => succ(e)
    }
    fua
  }

  def addEffectAnyway(inAnyCase: => Unit)(implicit ec: EC): Fu[A] = {
    fua onComplete { _ =>
      inAnyCase
    }
    fua
  }

}

final class PimpedFutureBoolean(private val fua: Fu[Boolean]) extends AnyVal {

  @inline def unary_! = fua.map { !_ }(EC.parasitic)

}

final class PimpedFutureOption[A](private val fua: Future[Option[A]]) extends AnyVal {

  def orFail(msg: => String)(implicit ec: EC): Fu[A] = fua flatMap {
    _.fold[Fu[A]](fufail(msg))(fuccess(_))
  }

  def getOrElse(other: => Fu[A])(implicit ec: EC): Fu[A] = fua flatMap { _.fold(other)(fuccess) }

  def map2[B](f: A => B)(implicit ec: EC): Fu[Option[B]] = fua.map(_ map f)
  def dmap2[B](f: A => B): Fu[Option[B]] = fua.map(_ map f)(EC.parasitic)
  
}

final class PimpedIterableFuture[A, M[X] <: IterableOnce[X]](private val t: M[Fu[A]]) extends AnyVal {
  def sequenceFu(implicit bf: BuildFrom[M[Fu[A]], A, M[A]], ec: EC): Fu[M[A]] = Future.sequence(t)
}
