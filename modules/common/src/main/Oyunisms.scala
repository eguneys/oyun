package oyun

import ornicar.scalalib
import oyun.base._

trait Oyunisms
    extends OyunTypes
    with scalalib.Common
    with scalalib.OrnicarOption
    with scalalib.Zeros
    with scalaz.std.OptionFunctions
    with scalaz.std.OptionInstances
    with scalaz.syntax.std.ToOptionIdOps
    with scalaz.syntax.ToIdOps 
    with scalaz.syntax.ToMonoidOps {


  @inline implicit def toPimpedFuture[A](f: Fu[A]) = new PimpedFuture(f)

  @inline implicit def toPimpedBoolean(b: Boolean) = new PimpedBoolean(b)

  @inline implicit def toPimpedOption[A](a: Option[A]) = new PimpedOption(a)

}
