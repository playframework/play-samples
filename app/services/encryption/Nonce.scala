package services.encryption

import org.abstractj.kalium.crypto.Random

/**
 * Nonces are used to ensure that encryption is completely random.  They should be generated once per encryption.
 *
 * You can store and display nonces -- they are not confidential -- but you must never reuse them, ever.
 */
class Nonce(val raw: Array[Byte]) extends AnyVal

object Nonce {

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
