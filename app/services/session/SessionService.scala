package services.session

import javax.inject.{Inject, Singleton}

import play.api.cache.SyncCacheApi

/**
 * A session service that ties session id to secret key.  This would probably be a
 * key value store like Redis or Cassandra in a production system.
 *
 * @param cache
 */
@Singleton
class SessionService @Inject()(cache: SyncCacheApi) {

  def create(secretKey: Array[Byte]): String = {
    val sessionId = newSessionId()
    cache.set(sessionId, secretKey)
    sessionId
  }

  def lookup(sessionId: String): Option[Array[Byte]] = {
    cache.get[Array[Byte]](sessionId)
  }

  def put(sessionId: String, sessionKey: Array[Byte]): Unit = {
    cache.set(sessionId, sessionKey)
  }

  def delete(sessionId: String): Unit = {
    cache.remove(sessionId)
  }

  private val sr = new java.security.SecureRandom()

  private def newSessionId(): String = {
    new java.math.BigInteger(130, sr).toString(32)
  }
}
