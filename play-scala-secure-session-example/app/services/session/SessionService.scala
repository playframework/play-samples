package services.session

import javax.inject.{ Inject, Singleton }

import org.apache.pekko.actor.typed.{ ActorRef, Scheduler }
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.util.Timeout
import services.session.SessionCache._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * A session service that ties session id to secret key using pekko CRDTs
 */
@Singleton
class SessionService @Inject() (cacheActor: ActorRef[Command])(implicit ec: ExecutionContext, scheduler: Scheduler) {
  implicit private val timeout: Timeout = org.apache.pekko.util.Timeout(300.milliseconds)

  def create(secretKey: Array[Byte]): Future[String] = {
    val sessionId = newSessionId()
    cacheActor ! PutInCache(sessionId, secretKey)
    Future.successful(sessionId)
  }

  def lookup(sessionId: String): Future[Option[Array[Byte]]] = {
    (cacheActor ? (GetFromCache(sessionId, _))).map {
      case Cached(key: Any, value: Option[_]) =>
        value.asInstanceOf[Option[Array[Byte]]]
    }
  }

  def put(sessionId: String, secretKey: Array[Byte]): Future[Unit] = {
    cacheActor ! PutInCache(sessionId, secretKey)
    Future.successful(())
  }

  def delete(sessionId: String): Future[Unit] = {
    cacheActor ! Evict(sessionId)
    Future.successful(())
  }

  private val sr = new java.security.SecureRandom()

  private def newSessionId(): String = {
    new java.math.BigInteger(130, sr).toString(32)
  }
}
