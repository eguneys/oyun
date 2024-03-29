package oyun

import ornicar.scalalib
import org.joda.time.DateTime
import play.api.libs.json.{ JsObject, JsValue }
import oyun.base._

trait Oyunisms
    extends OyunTypes
    with scalalib.Common
    with scalalib.OrnicarNonEmptyList
    with scalalib.OrnicarOption
    with scalalib.Zeros
    with scalalib.Zero.Syntax
    with scalalib.Validation
    with scalaz.std.ListFunctions
    with scalaz.std.ListInstances
    with scalaz.std.OptionFunctions
    with scalaz.std.OptionInstances
    with scalaz.syntax.std.ToListOps
    with scalaz.syntax.std.ToOptionIdOps
    with scalaz.syntax.ToIdOps 
    with scalaz.syntax.ToMonoidOps
    with scalaz.syntax.ToShowOps
    with scalaz.syntax.ToValidationOps {

  type StringValue = oyun.base.OyunTypes.StringValue


  @inline implicit def toPimpedFuture[A](f: Fu[A]) = new PimpedFuture(f)
  @inline implicit def toPimpedFutureBoolean(f: Fu[Boolean]) = new PimpedFutureBoolean(f)
  @inline implicit def toPimpedFutureOption[A](f: Fu[Option[A]]) = new PimpedFutureOption(f)
  @inline implicit def toPimpedIterableFuture[A, M[X] <: IterableOnce[X]](t: M[Fu[A]]) =
    new PimpedIterableFuture(t)

  @inline implicit def toPimpedJsObject(jo: JsObject) = new PimpedJsObject(jo)


  @inline implicit def toPimpedBoolean(b: Boolean) = new PimpedBoolean(b)
  @inline implicit def toPimpedInt(b: Int) = new PimpedInt(b)

  @inline implicit def toPimpedOption[A](a: Option[A]) = new PimpedOption(a)
  @inline implicit def toPimpedString(s: String) = new PimpedString(s)
  @inline implicit def toPimpedDateTime(d: DateTime)             = new PimpedDateTime(d)



}
