/*
Copyright (c) 2014 Graham Edgecombe <graham@grahamedgecombe.com>
Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/
package https

import java.net.Socket
import java.security.cert.X509Certificate
import java.security.{Principal, PrivateKey}
import javax.net.ssl._

import org.slf4j.Logger

/**
 * This class picks out a specific certificate when given a request that has
 * an SNI hostname associated with it.  This is done through
 * chooseEngineServerAlias, which under normal circumstances return null, but
 * in this case should return the alias associated with the sni hostname.
 *
 * Taken from https://github.com/grahamedgecombe/netty-sni-example
 */
final class SniKeyManager(val keyManager: X509ExtendedKeyManager, val defaultAlias: Option[String]) extends X509ExtendedKeyManager {

  private val logger: Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def getClientAliases(keyType: String, issuers: Array[Principal]): Array[String] = {
    throw new UnsupportedOperationException
  }

  override def chooseClientAlias(keyType: Array[String], issuers: Array[Principal], socket: Socket): String = {
    throw new UnsupportedOperationException
  }

  override def chooseEngineClientAlias(keyType: Array[String], issuers: Array[Principal], engine: SSLEngine): String = {
    throw new UnsupportedOperationException
  }

  override def getServerAliases(keyType: String, issuers: Array[Principal]): Array[String] = {
    keyManager.getServerAliases(keyType, issuers)
  }

  override def chooseServerAlias(keyType: String, issuers: Array[Principal], socket: Socket): String = {
    throw new UnsupportedOperationException
  }

  /**
   * Returns an alias (a name to pick out of the keystore) when given a hostname.
   */
  override def chooseEngineServerAlias(keyType: String, issuers: Array[Principal], engine: SSLEngine): String = {
    val session: ExtendedSSLSession = engine.getHandshakeSession.asInstanceOf[ExtendedSSLSession]

    getSNIHostname(session) match {
     case Some(hostname) if hasCertChain(hostname) && hasPrivateKey(hostname) =>
       logger.debug("chooseEngineServerAlias: using selected sniHostname {} as server alias", hostname)
       hostname
     case _ =>
       defaultAlias match {
         case Some(alias) =>
           logger.debug("chooseEngineServerAlias: using defaultAlias {} as server alias", defaultAlias)
           alias
         case None =>
           logger.debug("chooseEngineServerAlias: no alias found, using super method")
           super.chooseEngineServerAlias(keyType, issuers, engine)
       }
    }
  }

  override def getCertificateChain(alias: String): Array[X509Certificate] = {
     keyManager.getCertificateChain(alias)
  }

  override def getPrivateKey(alias: String): PrivateKey = {
     keyManager.getPrivateKey(alias)
  }

  private def getSNIHostname(session: ExtendedSSLSession): Option[String] = {
    import scala.collection.JavaConverters._
    session.getRequestedServerNames.asScala.find { name =>
      name.getType == StandardConstants.SNI_HOST_NAME
    }.map {
      case name: SNIHostName =>
        name.getAsciiName
    }
  }

  private def hasCertChain(hostname: String): Boolean = getCertificateChain(hostname) != null

  private def hasPrivateKey(hostname: String): Boolean = getPrivateKey(hostname) != null

}
