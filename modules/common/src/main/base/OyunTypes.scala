package oyun.base

import scala.concurrent.Future

import ornicar.scalalib.{ ValidTypes, Zero }

trait OyunTypes extends ValidTypes {

  type Fu[A] = Future[A]
  type Funit = Fu[Unit]

  @inline def fuccess[A](a: A): Fu[A] = Future.successful(a)
  def fufail[X](t: Throwable): Fu[X] = Future.failed(t)
  def fufail[X](s: String): Fu[X] = fufail(OyunException(s))
  val funit = fuccess(())
  val fuTrue = fuccess(true)
  val fuFalse = fuccess(false)



}

object OyunTypes extends OyunTypes {


}
