package oyun.base

import OyunTypes._
import scala.concurrent.{ ExecutionContext => EC }

final class PimpedFuture[A](private val fua: Fu[A]) extends AnyVal {

  @inline def dmap[B](f: A => B): Fu[B] = fua.map(f)(EC.parasitic)

}
