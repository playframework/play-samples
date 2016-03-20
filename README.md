# Play Kalium

This is an example application that shows how to use symmetric encryption with [Kalium](https://github.com/abstractj/kalium/).

You must install libsodium before using this application.  If you have homebrew, you can use `brew install libsodium`.

The credit card encryption service is where you'll find the symmetric encryption code.

To use the encryption service, add something like this to a controller:

## Controller Usage Example

```scala
@Singleton
class HomeController @Inject()(creditCardEncryptionService: CreditCardEncryptionService) extends Controller {

  // Save the nonce and the ciphertext to a database under normal circumstances. 
  // Nonces are not confidential, so if you need to, you can pass them in a
  // query parameter for decryption.
  //
  // Note that nonces should never be reused (nonce stands for N="once")
  val cipherPair: (Nonce, Array[Byte]) = {
    val ccNumber = "4111 1111 1111 1111".getBytes(StandardCharsets.UTF_8)
    creditCardEncryptionService.encrypt(ccNumber)
  }

  def index = Action {
    val decryptedBytes = creditCardEncryptionService.decrypt(cipherPair)
    val ccNumber = new String(decryptedBytes, StandardCharsets.UTF_8)

    Ok(views.html.index(ccNumber))
  }

}
```

That's it.
