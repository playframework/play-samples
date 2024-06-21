package https

import java.nio.file._
import java.security.KeyStore
import javax.net.ssl._

import play.core.ApplicationProvider
import play.server.api._
import play.core.server._

class CustomSSLEngineProvider(
  serverConfig: ServerConfig,
  appProvider: ApplicationProvider
) extends SSLEngineProvider {

  val certificateDirectory: String =
    serverConfig.configuration.getOptional[String]("certificateDirectory").getOrElse(
      s"${System.getProperty("user.home")}/.certificates")

  def readPassword(): Array[Char] = {
    val passwordPath = FileSystems.getDefault.getPath(certificateDirectory, "password")
    Files.readAllLines(passwordPath).get(0).toCharArray
  }

  def readKeyInputStream(): java.io.InputStream = {
    val keyPath = FileSystems.getDefault.getPath(certificateDirectory, "example.com.p12")
    Files.newInputStream(keyPath)
  }

  def readTrustInputStream(): java.io.InputStream = {
    val keyPath = FileSystems.getDefault.getPath(certificateDirectory, "clientca.p12")
    Files.newInputStream(keyPath)
  }

  def readKeyManagers(): Array[KeyManager] = {
    val password = readPassword()
    val keyInputStream = readKeyInputStream()
    try {
      val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      keyStore.load(keyInputStream, password)
      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(keyStore, password)

      // Now that we have the key manager, we have to extend it with SNIKeyManager so we
      // get the extendedEngineAlias
      val keyManagers = kmf.getKeyManagers
      val onlyKeyManager: X509ExtendedKeyManager = keyManagers(0).asInstanceOf[X509ExtendedKeyManager]
      val defaultAlias = Some("wildcard.example.com")
      val sniKeyManager = new SniKeyManager(onlyKeyManager, defaultAlias)
      Array(sniKeyManager)
    } finally {
      keyInputStream.close()
    }
  }

  def readTrustManagers(): Array[TrustManager] = {
    val password = readPassword()
    val trustInputStream = readTrustInputStream()
    try {
      val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      keyStore.load(trustInputStream, password)
      val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
      tmf.init(keyStore)
      tmf.getTrustManagers
    } finally {
      trustInputStream.close()
    }
  }

  private def createSSLContext(applicationProvider: ApplicationProvider): SSLContext = {
    val keyManagers = readKeyManagers()
    val trustManagers = readTrustManagers()

    // Configure the SSL context to use TLS
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagers, trustManagers, null)
    sslContext
  }

  override def sslContext(): SSLContext = createSSLContext(appProvider)

  override def createSSLEngine(): SSLEngine = {
    val sslContext = this.sslContext()

    // Start off with a clone of the default SSL parameters...
    val sslParameters = sslContext.getDefaultSSLParameters

    // Tells the server to ignore client's cipher suite preference.
    // https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-EFC2FACC-680C-42CE-A3A9-E9A6673EA813
    sslParameters.setUseCipherSuitesOrder(true)

    // https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-BC9AD59B-05B6-4ACA-9CDD-D18ACEA3840D
    val needClientAuth = java.lang.System.getProperty("play.ssl.needClientAuth")
    sslParameters.setNeedClientAuth(java.lang.Boolean.parseBoolean(needClientAuth))

    // Clone and modify the default SSL parameters.
    val engine = sslContext.createSSLEngine
    engine.setSSLParameters(sslParameters)

    engine
  }

}
