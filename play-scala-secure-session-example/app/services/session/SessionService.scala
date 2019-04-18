package services.session

import javax.inject.{ Inject, Named, Singleton }

import akka.actor.ActorRef
import akka.pattern.ask
import services.session.SessionCache._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * A session service that ties session id to secret key using akka CRDTs
 */
@Singleton
class SessionService @Inject() (@Named("replicatedCache") cacheActor: ActorRef)(implicit ec: ExecutionContext) {

  implicit def akkaTimeout = akka.util.Timeout(300.milliseconds)

  def create(secretKey: Array[Byte]): Future[String] = {
    val sessionId = newSessionId()
    cacheActor ! PutInCache(sessionId, secretKey)
    Future.successful(sessionId)
  }

  def lookup(sessionId: String): Future[Option[Array[Byte]]] = {
    (cacheActor ? GetFromCache(sessionId)).map {
      case Cached(key: Any, value: Option[_]) =>
        value.asInstanceOf[Option[Array[Byte]]]
    }
  }

  def put(sessionId: String, secretKey: Array[Byte]): Future[Unit] = {
    cacheActor ! PutInCache(sessionId, secretKey)
    Future.successful(())
  }

  def delete(sessionId: String): Future[Unit] = {
    cacheActor ? Evict(sessionId)
    Future.successful(())
  }

  private val sr = new java.security.SecureRandom()

  private def newSessionId(): String = {
    new java.math.BigInteger(130, sr).toString(32)
  }
}
