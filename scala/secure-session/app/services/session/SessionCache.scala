package services.session

import org.apache.pekko.actor.Cancellable
import org.apache.pekko.actor.typed.{ ActorRef, Behavior, PostStop }
import org.apache.pekko.actor.typed.scaladsl.{ ActorContext, Behaviors }
import org.apache.pekko.cluster.ddata.typed.scaladsl.ReplicatorMessageAdapter
import org.apache.pekko.cluster.ddata.LWWMap

import scala.concurrent.duration._
import org.apache.pekko.cluster.ddata.SelfUniqueAddress

/**
 * A replicated key-store map using pekko distributed data. The advantage of
 * replication over distributed cache is that all the sessions are local on
 * every machine, so there's no remote lookup necessary.
 *
 * Note that this doesn't serialize using protobuf and also isn't being sent over SSL,
 * so it's still not as secure as it could be.  Please see https://pekko.apache.org/docs/pekko/current/scala/remoting-artery.html#remote-security
 * for more details.
 *
 * https://pekko.apache.org/docs/pekko/current/scala/distributed-data.html
 */
class SessionCache(
    context: ActorContext[SessionCache.Command],
    replicator: ReplicatorMessageAdapter[SessionCache.Command, LWWMap[String, Array[Byte]]],
) {
  //  This is from one of the examples covered in the pekko distributed data section:
  // https://github.com/apache/incubator-pekko-samples/blob/HEAD/pekko-sample-distributed-data-scala/src/main/scala/sample/distributeddata/ReplicatedCache.scala

  import SessionCache._
  import SessionExpiration._
  import org.apache.pekko.cluster.ddata.{ LWWMap, LWWMapKey }
  import org.apache.pekko.cluster.ddata.typed.scaladsl.DistributedData
  import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator.{ Command => _, _ }
  import context.log

  private val expirationTime: FiniteDuration = {
    val expirationString = context.system.settings.config.getString("session.expirationTime")
    Duration(expirationString).asInstanceOf[FiniteDuration]
  }

  private val distributedData: DistributedData = DistributedData(context.system)
  private[this] implicit val uniqAddress: SelfUniqueAddress = distributedData.selfUniqueAddress

  def behavior(children: Map[String, ActorRef[RefreshSession.type]]): Behavior[Command] = Behaviors.receiveMessage {
    case PutInCache(key, value) =>
      replicator.askUpdate(Update(dataKey(key), emptyMap, WriteLocal, _)(_ :+ (key -> value)), nop)
      refreshSessionExpiration(key, children)

    case Evict(key) =>
      destroySessionExpiration(key)
      replicator.askUpdate(Update(dataKey(key), emptyMap, WriteLocal, _)(_.remove(uniqAddress, key)), nop)
      Behaviors.same

    case GetFromCache(key, replyTo) =>
      replicator.askGet(Get(dataKey(key), ReadLocal, _), InternalGetResponse(_, replyTo))
      Behaviors.same

    case InternalGetResponse(g @ GetSuccess(mk @ LWWMapKey(key)), replyTo) =>
      replyTo ! Cached(key, g.get(mk).get(key))
      refreshSessionExpiration(key, children)

    case InternalGetResponse(NotFound(LWWMapKey(key)), replyTo) =>
      replyTo ! Cached(key, None)
      Behaviors.same

    case InternalSessionActorTerminated(key) =>
      behavior(children - key)

    case _: InternalGetResponse =>
      Behaviors.same

    case _: InternalUpdateResponse[_] =>
      Behaviors.same
  }

  private def emptyMap: LWWMap[String, Array[Byte]]                = LWWMap.empty
  private def dataKey(key: String): LWWMapKey[String, Array[Byte]] = LWWMapKey(key)
  private def nop[A](x: A)                                         = InternalUpdateResponse(x)

  private def refreshSessionExpiration(key: String, children: Map[String, ActorRef[RefreshSession.type]]): Behavior[Command] = {
    children.get(key) match {
      case Some(sessionInstance) =>
        log.info(s"Refreshing session $key")
        sessionInstance ! RefreshSession
        Behaviors.same
      case None =>
        log.info(s"Creating new session $key")
        val sessionInstance = context.spawn(SessionExpiration(context.self, key, expirationTime), key)
        context.watchWith(sessionInstance, InternalSessionActorTerminated(key))
        behavior(children + (key -> sessionInstance))
    }
  }

  private def destroySessionExpiration(key: String) = {
    log.info(s"Destroying session $key")
    context.child(key).foreach(context.stop)
  }

}

object SessionCache {
  import org.apache.pekko.cluster.ddata.LWWMap
  import org.apache.pekko.cluster.ddata.typed.scaladsl.DistributedData
  import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator._

  sealed trait Command

  final case class PutInCache(key: String, value: Array[Byte]) extends Command

  final case class GetFromCache(key: String, replyTo: ActorRef[Cached]) extends Command

  final case class Cached(key: String, value: Option[Array[Byte]])

  final case class Evict(key: String) extends Command

  private sealed trait InternalCommand extends Command

  private final case class InternalGetResponse(
      rsp: GetResponse[LWWMap[String, Array[Byte]]],
      replyTo: ActorRef[Cached],
  ) extends InternalCommand

  private case class InternalUpdateResponse[A](x: A) extends InternalCommand

  private case class InternalSessionActorTerminated(key: String) extends InternalCommand

  def apply() = Behaviors.setup[Command] { context =>
    DistributedData.withReplicatorMessageAdapter[Command, LWWMap[String, Array[Byte]]] { replicator =>
      new SessionCache(context, replicator).behavior(Map.empty)
    }
  }
}

object SessionExpiration {
  case object RefreshSession
  import SessionCache.Evict

  def apply(parent: ActorRef[Evict], key: String, expirationTime: FiniteDuration): Behavior[RefreshSession.type] = {
    Behaviors.setup { context =>
      var maybeCancel: Option[Cancellable] = None

      def schedule()   = { maybeCancel = Some(context.scheduleOnce(expirationTime, parent, Evict(key))) }
      def cancel()     = { maybeCancel.foreach(_.cancel()) }
      def reschedule() = { cancel(); schedule() }

      schedule()

      Behaviors.logMessages(
        Behaviors
          .receiveMessage[RefreshSession.type] { case RefreshSession => reschedule(); Behaviors.same }
          .receiveSignal { case (_, PostStop) => cancel(); Behaviors.same }
      )
    }
  }
}
