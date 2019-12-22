package oyun

import oyun.base._

trait Oyunisms
    extends OyunTypes {


  @inline implicit def toPimpedFuture[A](f: Fu[A]) = new PimpedFuture(f)

}
