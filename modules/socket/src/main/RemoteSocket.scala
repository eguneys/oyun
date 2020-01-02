package oyun.socket

import akka.actor.{ ActorSystem }
import io.lettuce.core._
import scala.concurrent.{ Future, Promise }

final class RemoteSocket(
  redisClient: RedisClient
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {

  import RemoteSocket._
  import Protocol._


  val baseHandler: Handler = {
    case In.ConnectUser(userId) =>
      println("connect user", userId)
    case In.WsBoot =>
      logger.warn("Remote socket boot")
  }


  def subscribe(channel: Channel, reader: In.Reader)(handler: Handler): Future[Unit] = {
    val conn = redisClient.connectPubSub()
    conn.addListener(new pubsub.RedisPubSubAdapter[String, String] {
      override def message(_channel: String, message: String): Unit =
        reader(RawMsg(message)) collect handler match {
          case Some(_) => // processed
          case None => logger.warn(s"Unhandled $channel $message")
        }
    })
    val subPromise = Promise[Unit]
    conn.async.subscribe(channel).thenRun {
      new Runnable { def run() = subPromise.success(()) }
    }
    subPromise.future
  }
  
}


object RemoteSocket {

  private val logger = oyun log "socket"

  type Send = String => Unit

  trait Sender {
    def apply(msg: String): Unit
  }
  

  object Protocol {

    final class RawMsg(val path: Path, val args: Args) {

    }
    def RawMsg(msg: String): RawMsg = {
      val parts = msg.split(" ", 2)
      new RawMsg(parts(0), ~parts.lift(1))
    }


    trait In
    object In {

      type Reader = RawMsg => Option[In]

      case object WsBoot extends In
      case class ConnectUser(userId: String) extends In


      val baseReader: Reader = raw =>
      raw.path match {
        case "connect/user" => ConnectUser(raw.args).some
        case "boot" => WsBoot.some
        case _ => None
      }
    }

  }

  type Channel = String
  type Path = String
  type Args = String
  type Handler = PartialFunction[Protocol.In, Unit]
}
