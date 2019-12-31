package oyun.base

import scala.concurrent.Future

import ornicar.scalalib.{ ValidTypes, Zero }
import play.api.libs.json.{ JsObject }

trait OyunTypes extends ValidTypes {

  trait IntValue extends Any {
    def value: Int
    override def toString = value.toString
  }

  trait BooleanValue extends Any {
    def value: Boolean
    override def toString = value.toString
  }

  type Fu[A] = Future[A]
  type Funit = Fu[Unit]

  @inline def fuccess[A](a: A): Fu[A] = Future.successful(a)
  def fufail[X](t: Throwable): Fu[X] = Future.failed(t)
  def fufail[X](s: String): Fu[X] = fufail(OyunException(s))
  val funit = fuccess(())
  val fuTrue = fuccess(true)
  val fuFalse = fuccess(false)


  implicit val fUnitZero: Zero[Fu[Unit]] = Zero.instance(funit)
  implicit val fBooleanZero: Zero[Fu[Boolean]] = Zero.instance(fuFalse)

  implicit def fuZero[A](implicit az: Zero[A]) = new Zero[Fu[A]] {
    def zero = fuccess(az.zero)
  }

  implicit val jsObjectZero = Zero.instance(JsObject(Seq.empty))


}

object OyunTypes extends OyunTypes {

  trait StringValue extends Any {
    def value: String
    override def toString = value
  }


}
