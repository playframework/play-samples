package services.creditcard

import java.security.SecureRandom
import javax.inject.{Inject, Singleton}

import org.abstractj.kalium.crypto.{Random, Util}
import play.api.Configuration

/**
  * For every service you need, you should specify a specific crypto service with its own keys.
  *
  * That is, if you have a service which encrypts credit cards, and another service which encrypts S3 credentials, they
  * should not reuse the key.  If you use the same key for both, then an attacker can cross reference between
  * the encrypted values and reconstruct the key.  This rule applies even if you are sharing the same key for hashing
  * and encryption.
  *
  * Keeping distinct keys per service is known as the "key separation principle".
  */
@Singleton
class CreditCardEncryptionService @Inject()(configuration: Configuration) {
  import CreditCardEncryptionService._

  type CipherText = Array[Byte]

  type CipherPair = (Nonce, CipherText)

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
    // Storing key information confidentially is hard -- use an HSM or an encrypted filesystem or secret service.
    val secretHex: String = configuration.getString("creditcard.crypto.secret").getOrElse {
      val randomSecret = encoder.encode(newSecretKey)
      logger.info(s"No secret found, creating temporary secret ${randomSecret}")
      randomSecret
    }
    val secret = encoder.decode(secretHex)
    new org.abstractj.kalium.crypto.SecretBox(secret)
  }

  def encrypt(message: String): CipherPair = {
    val nonce = createNonce()
    val cipherText = box.encrypt(nonce.raw, encoder.decode(message))
    (nonce, cipherText)
  }

  def encrypt(message: Array[Byte]): CipherPair = {
    val nonce = createNonce()
    val cipherText = box.encrypt(nonce.raw, message)
    (nonce, cipherText)
  }

  def decrypt(nonce: Nonce, cypherText: CipherText): Array[Byte] = {
    box.decrypt(nonce.raw, cypherText)
  }

  def decrypt(pair: CipherPair): Array[Byte] = {
    box.decrypt(pair._1.raw, pair._2)
  }

}

object CreditCardEncryptionService {
  // No real advantage over java.secure.SecureRandom, or a call to /dev/urandom
  private val random = new Random()

  /**
    * Nonces are used to ensure that encryption is completely random.  They should be generated once per encryption.
    *
    * You can store and display nonces -- they are not confidential -- but you must never reuse them, ever.
    *
    * We make it very easy to use nonces correctly here, because createNonce() is private, and encrypt() creates a
    * nonce under the hood automatically and returns the nonce with the associated ciphertext.
    */
  class Nonce private[CreditCardEncryptionService](val raw: Array[Byte])

  /**
    * Creates a random nonce value.
    */
  private def createNonce(): Nonce = {
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES
    new Nonce(random.randomBytes(XSALSA20_POLY1305_SECRETBOX_NONCEBYTES))
  }

  /**
    * Reconstitute a nonce that has been stored with a ciphertext.
    */
  def fromBytes(data: Array[Byte]): Nonce = {
    import org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES
    if (data == null || data.length != XSALSA20_POLY1305_SECRETBOX_NONCEBYTES) {
      throw new IllegalArgumentException("This createNonce has an invalid size: " + data.length)
    }
    new Nonce(data)
  }

}
