package oyun.socket

import akka.actor.{ ActorSystem, CoordinatedShutdown }
import io.lettuce.core._
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import play.api.libs.json._
import scala.concurrent.{ Future, Promise }


import oyun.common.{ Bus, Oyunakka }
import oyun.hub.actorApi.socket.remote.{ TellSriIn, TellSriOut }
import Socket.Sri



final class RemoteSocket(
  redisClient: RedisClient,
  shutdown: CoordinatedShutdown
)(implicit ec: scala.concurrent.ExecutionContext, system: ActorSystem) {

  import RemoteSocket._
  import Protocol._

  private var stopping = false

  val baseHandler: Handler = {
    case In.TellSri(sri, userId, typ, msg) =>
      Bus.publish(TellSriIn(sri.value, userId, msg), s"remoteSocketIn:$typ")
    case In.WsBoot =>
      logger.warn("Remote socket boot")
  }

  final class StoppableSender(conn: StatefulRedisPubSubConnection[String, String], channel: Channel) extends Sender {

    def apply(msg: String): Unit = if (!stopping) conn.async.publish(channel, msg)

  }

  def makeSender(channel: Channel): Sender = new StoppableSender(redisClient.connectPubSub(), channel)


  def subscribe(channel: Channel, reader: In.Reader)(handler: Handler): Future[Unit] = {
    val conn = redisClient.connectPubSub()
    conn.addListener(new pubsub.RedisPubSubAdapter[String, String] {
      override def message(_channel: String, message: String): Unit =
        reader(RawMsg(message)) collect handler match {
          case Some(_) => // processed
          case None => {
            logger.warn(s"Unhandled $channel $message")
          }
        }
    })
    val subPromise = Promise[Unit]
    conn.async.subscribe(channel).thenRun {
      new Runnable { def run() = subPromise.success(()) }
    }
    subPromise.future
  }





  Oyunakka.shutdown(shutdown, _.PhaseServiceUnbind, "Stopping the socket redis pool") { () =>
    Future {
      stopping = true
      redisClient.shutdown()
    }
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
      def get(nb: Int)(f: PartialFunction[Array[String], Option[In]]): Option[In] =
        f.applyOrElse(args.split(" ", nb), (_: Array[String]) => None)
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
      case class ConnectSris(cons: Iterable[(Sri, Option[String])]) extends In
      case class DisconnectSris(sris: Iterable[Sri]) extends In
      case class TellSri(sri: Sri, userId: Option[String], typ: String, msg: JsObject) extends In


      val baseReader: Reader = raw =>
      raw.path match {
        case "connect/user" => ConnectUser(raw.args).some
        case "tell/sri" => raw.get(3)(tellSriMapper)
        case "boot" => WsBoot.some
        case _ => None
      }

      def tellSriMapper: PartialFunction[Array[String], Option[TellSri]] = {
        case Array(sri, user, payload) =>
          for {
            obj <- Json.parse(payload).asOpt[JsObject]
            typ <- obj str "t"
          } yield TellSri(Sri(sri), optional(user), typ, obj)
      }

      def optional(str: String): Option[String] = if (str == "-") None else Some(str)
    }


    object Out {

      def tellSri(sri: Sri, payload: JsValue) =
        s"tell/sri $sri ${Json stringify payload}"

    }
  }

  type Channel = String
  type Path = String
  type Args = String
  type Handler = PartialFunction[Protocol.In, Unit]
}
