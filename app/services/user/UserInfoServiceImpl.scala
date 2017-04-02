package services.user

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.inject.{Inject, Singleton}

import org.abstractj.kalium.crypto.Random
import play.api.Configuration
import play.api.libs.json.{JsResult, Json}


/**
  * Implementation of user info service.
  */
@Singleton
class UserInfoServiceImpl @Inject()(configuration: Configuration) extends UserInfoService {

  private val encoder = org.abstractj.kalium.encoders.Encoder.HEX

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  // utility method for when we're showing off secret key without saving confidential info...
  private def newSecretKey: Array[Byte] = {
    // Key must be 32 bytes for secretbox
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_KEYBYTES
    val buf = new Array[Byte](XSALSA20_POLY1305_SECRETBOX_KEYBYTES)
    new SecureRandom().nextBytes(buf)
    buf
  }

  private val box = {
    /*
     * For every service you need, you should specify a specific crypto service with its own keys.   Keeping distinct
     * keys per service is known as the "key separation principle".
     *
     * More specifically, if you have a service which encrypts user information, and another service which encrypts
     * S3 credentials, they should not reuse the key.  If you use the same key for both, then an attacker can cross
     * reference between the encrypted values and reconstruct the key.  This rule applies even if you are sharing
     * the same key for hashing and encryption.
     *
     * Storing key information confidentially and doing key rotation properly is a specialized area. Check out Daniel Somerfield's talk: <a href="https://youtu.be/OUSvv2maMYI">Turtles All the Way Down: Storing Secrets in the Cloud and the Data Center</a> for the details.
     */
    val secretHex: String = configuration.getOptional[String]("user.crypto.secret").getOrElse {
      val randomSecret = encoder.encode(newSecretKey)
      logger.info(s"No secret found, creating temporary secret ${randomSecret}")
      randomSecret
    }
    val secret = encoder.decode(secretHex)
    new org.abstractj.kalium.crypto.SecretBox(secret)
  }

  override def encrypt(userInfo: UserInfo): Map[String, String] = {
    val nonce = Nonce.createNonce()
    val json = Json.toJson(userInfo)
    val stringData = Json.stringify(json)
    val rawData = stringData.getBytes(StandardCharsets.UTF_8)
    val cipherText = box.encrypt(nonce.raw, rawData)

    val nonceHex = encoder.encode(nonce.raw)
    val cipherHex = encoder.encode(cipherText)
    Map("nonce" -> nonceHex, "c" -> cipherHex)
  }

  override def decrypt(data: Map[String, String]): UserInfo = {
    val nonceHex = data("nonce")
    val nonce = Nonce.nonceFromBytes(encoder.decode(nonceHex))
    val cipherTextHex = data("c")
    val cipherText = encoder.decode(cipherTextHex)
    val rawData = box.decrypt(nonce.raw, cipherText)
    val stringData = new String(rawData, StandardCharsets.UTF_8)
    val json = Json.parse(stringData)
    val result: JsResult[UserInfo] = Json.fromJson[UserInfo](json) // uses UserInfo.format JSON magic.
    result.get
  }

}

/**
  * Nonces are used to ensure that encryption is completely random.  They should be generated once per encryption.
  *
  * You can store and display nonces -- they are not confidential -- but you must never reuse them, ever.
  */
private[user] class Nonce(val raw: Array[Byte]) extends AnyVal

private[user] object Nonce {

  // No real advantage over java.secure.SecureRandom, or a call to /dev/urandom
  private val random = new Random()

  /**
    * Creates a random nonce value.
    */
  def createNonce(): Nonce = {
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES
    new Nonce(random.randomBytes(XSALSA20_POLY1305_SECRETBOX_NONCEBYTES))
  }

  /**
    * Reconstitute a nonce that has been stored with a ciphertext.
    */
  def nonceFromBytes(data: Array[Byte]): Nonce = {
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES
    if (data == null || data.length != XSALSA20_POLY1305_SECRETBOX_NONCEBYTES) {
      throw new IllegalArgumentException("This nonce has an invalid size: " + data.length)
    }
    new Nonce(data)
  }

}
