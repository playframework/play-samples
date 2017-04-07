package services.user

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.inject.{Inject, Singleton}

import org.abstractj.kalium.crypto.Random
import play.api.Configuration
import play.api.libs.json.{JsResult, Json, OFormat}


case class UserInfo(username: String)

object UserInfo {

  // Use a JSON format to automatically convert between case class and JsObject
  implicit val format: OFormat[UserInfo] = Json.format[UserInfo]

}

/**
 * Implementation of user info service.
 */
@Singleton
class UserInfoService @Inject()(configuration: Configuration) {

  private val encoder = org.abstractj.kalium.encoders.Encoder.HEX

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  // utility method for when we're showing off secret key without saving confidential info...
  def newSecretKey: Array[Byte] = {
    // Key must be 32 bytes for secretbox
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_KEYBYTES
    val buf = new Array[Byte](XSALSA20_POLY1305_SECRETBOX_KEYBYTES)
    new SecureRandom().nextBytes(buf)
    buf
  }

  private def box(secretKey: Array[Byte]) = {
    new org.abstractj.kalium.crypto.SecretBox(secretKey)
  }

  def encrypt(secretKey: Array[Byte], userInfo: Option[UserInfo]): Map[String, String] = {
    val nonce = Nonce.createNonce()
    val json = Json.toJson(userInfo)
    val stringData = Json.stringify(json)
    val rawData = stringData.getBytes(StandardCharsets.UTF_8)
    val cipherText = box(secretKey).encrypt(nonce.raw, rawData)

    val nonceHex = encoder.encode(nonce.raw)
    val cipherHex = encoder.encode(cipherText)
    Map("nonce" -> nonceHex, "c" -> cipherHex)
  }

  def decrypt(secretKey: Array[Byte], data: Map[String, String]): Option[UserInfo] = {
    val nonceHex = data("nonce")
    val nonce = Nonce.nonceFromBytes(encoder.decode(nonceHex))
    val cipherTextHex = data("c")
    val cipherText = encoder.decode(cipherTextHex)
    val rawData = box(secretKey).decrypt(nonce.raw, cipherText)
    val stringData = new String(rawData, StandardCharsets.UTF_8)
    val json = Json.parse(stringData)
    val result: JsResult[UserInfo] = Json.fromJson[UserInfo](json) // uses UserInfo.format JSON magic.
    result.asOpt
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
