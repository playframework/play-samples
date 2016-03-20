package services.creditcard

import java.nio.charset.StandardCharsets

import org.scalatestplus.play._

class CreditCardEncryptionServiceSpec extends PlaySpec with OneAppPerTest {

  "encryption" should {

    "encrypt" in {
      val service = app.injector.instanceOf(classOf[CreditCardEncryptionService])
      val nonce = service.nonce()
      val cipherText = service.encrypt(nonce, "derp".getBytes(StandardCharsets.UTF_8))
      val decrypted = service.decrypt(nonce, cipherText)
      new String(decrypted, StandardCharsets.UTF_8) mustEqual("derp")
    }

  }

}
