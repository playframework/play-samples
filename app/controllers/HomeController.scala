package controllers

import java.nio.charset.StandardCharsets
import javax.inject._

import play.api.mvc._
import services.creditcard.CreditCardEncryptionService

@Singleton
class HomeController @Inject()(creditCardEncryptionService: CreditCardEncryptionService) extends Controller {

  // Save the nonce and the ciphertext to a database column.  Nonces are not confidential,
  // so if you need to you, you can pass them in a query parameter for decryption.
  // Note that nonces should never be reused (nonce stands for N="once")
  val cipherPair: (CreditCardEncryptionService.Nonce, Array[Byte]) = {
    val ccNumber = "4111 1111 1111 1111".getBytes(StandardCharsets.UTF_8)
    creditCardEncryptionService.encrypt(ccNumber)
  }

  def index = Action {
    val decryptedBytes = creditCardEncryptionService.decrypt(cipherPair)
    val ccNumber = new String(decryptedBytes, StandardCharsets.UTF_8)

    Ok(views.html.index(ccNumber))
  }

}
