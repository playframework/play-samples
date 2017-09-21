package services.session

import javax.inject.Inject

import akka.actor.{ Actor, ActorLogging, ActorRef, Cancellable, Props }
import akka.event.LoggingReceive

import scala.concurrent.duration._

/**
 * A replicated key-store map using akka distributed data. The advantage of
 * replication over distributed cache is that all the sessions are local on
 * every machine, so there's no remote lookup necessary.
 *
 * Note that this doesn't serialize using protobuf and also isn't being sent over SSL,
 * so it's still not as secure as it could be.  Please see http://doc.akka.io/docs/akka/current/scala/remoting-artery.html#remote-security 
 * for more details.
 *
 * http://doc.akka.io/docs/akka/current/scala/distributed-data.html
 */
class SessionCache extends Actor with ActorLogging {
  //  This is from one of the examples covered in the akka distributed data section:
  // https://github.com/akka/akka-samples/blob/master/akka-sample-distributed-data-scala/src/main/scala/sample/distributeddata/ReplicatedCache.scala
  import SessionCache._
  import SessionExpiration._
  import akka.cluster.Cluster
  import akka.cluster.ddata.{ DistributedData, LWWMap, LWWMapKey }
  import akka.cluster.ddata.Replicator._

  private val expirationTime: FiniteDuration = {
    val expirationString = context.system.settings.config.getString("session.expirationTime")
    Duration(expirationString).asInstanceOf[FiniteDuration]
  }

  private[this] val replicator = DistributedData(context.system).replicator
  private[this] implicit val cluster = Cluster(context.system)

  def receive = {

    case PutInCache(key, value) =>
      refreshSessionExpiration(key)
      replicator ! Update(dataKey(key), LWWMap(), WriteLocal)(_ + (key -> value))

    case Evict(key) =>
      destroySessionExpiration(key)
      replicator ! Update(dataKey(key), LWWMap(), WriteLocal)(_ - key)

    case GetFromCache(key) =>
      replicator ! Get(dataKey(key), ReadLocal, Some(Request(key, sender())))

    case g @ GetSuccess(LWWMapKey(_), Some(Request(key, replyTo))) =>
      refreshSessionExpiration(key)
      g.dataValue match {
        case data: LWWMap[_, _] => data.asInstanceOf[LWWMap[String, Array[Byte]]].get(key) match {
          case Some(value) => replyTo ! Cached(key, Some(value))
          case None => replyTo ! Cached(key, None)
        }
      }

    case NotFound(_, Some(Request(key, replyTo))) =>
      replyTo ! Cached(key, None)

    case _: UpdateResponse[_] => // ok
  }

  private def dataKey(key: String): LWWMapKey[String, Array[Byte]] = LWWMapKey(key)

  private def refreshSessionExpiration(key: String) = {
    context.child(key) match {
      case Some(sessionInstance) =>
        log.info(s"Refreshing session $key")
        sessionInstance ! RefreshSession
      case None =>
        log.info(s"Creating new session $key")
        context.actorOf(SessionExpiration.props(key, expirationTime), key)
    }
  }

  private def destroySessionExpiration(key: String) = {
    log.info(s"Destroying session $key")
    context.child(key).foreach(context.stop)
  }

}

object SessionCache {
  def props: Props = Props[SessionCache]

  final case class PutInCache(key: String, value: Array[Byte])

  final case class GetFromCache(key: String)

  final case class Cached(key: String, value: Option[Array[Byte]])

  final case class Evict(key: String)

  private final case class Request(key: String, replyTo: ActorRef)
}

class SessionExpiration(key: String, expirationTime: FiniteDuration) extends Actor with ActorLogging {
  import SessionExpiration._
  import services.session.SessionCache.Evict

  private var maybeCancel: Option[Cancellable] = None

  override def preStart(): Unit = {
    schedule()
  }

  override def postStop(): Unit = {
    cancel()
  }

  override def receive: Receive = LoggingReceive {
    case RefreshSession => reschedule()
  }

  private def cancel() = {
    maybeCancel.foreach(_.cancel())
  }

  private def reschedule(): Unit = {
    cancel()
    schedule()
  }

  private def schedule() = {
    val system = context.system
    maybeCancel = Some(system.scheduler.scheduleOnce(expirationTime, context.parent, Evict(key))(system.dispatcher))
  }
}

object SessionExpiration {
  def props(key: String, expirationTime: FiniteDuration) = Props(classOf[SessionExpiration], key, expirationTime)

  final case object RefreshSession
}
