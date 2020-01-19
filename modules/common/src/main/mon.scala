package oyun

object mon {


  object masa {
    object error {
      val client = counter("masa.error").withTag("from", "client")
    }

    val ductCount = gauge("masa.duct.count").withoutTags
  }

  private var backend: kamon.metric.MetricBuilding = _

  def start(enabled: Boolean): Unit = {
    backend = if (enabled) kamon.Kamon else new KamonStub
  }

  def destroy(): Unit = {
    backend = null
  }

  // private def timer(name: String) = backend.timer(name)
  private def gauge(name: String) = backend.gauge(name)
  private def counter(name: String) = backend.counter(name)
  // private def histogram(name: String) = backend.histogram(name)
}

// because using kamon.Kamon in dev mode
// causes leaks between reloads
final class KamonStub extends kamon.metric.MetricBuilding {

  import java.util.concurrent.{ Executors, ThreadFactory }
  import java.util.concurrent.atomic.AtomicInteger

  protected val registry = new kamon.metric.MetricRegistry(
    com.typesafe.config.ConfigFactory.load(kamon.ClassLoading.classLoader()),
    Executors.newScheduledThreadPool(
      1,
      new ThreadFactory {
        val count          = new AtomicInteger()
        val defaultFactory = Executors.defaultThreadFactory()
        override def newThread(r: Runnable): Thread = {
          val thread = defaultFactory.newThread(r)
          thread.setName("kamon-scheduler-" + count.incrementAndGet().toString)
          thread.setDaemon(true)
          thread
        }
      }
    ),
    new kamon.util.Clock.Anchored()
  )
}
