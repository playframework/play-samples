# Play Kalium

This is an example application that shows how to use symmetric encryption with [Kalium](https://github.com/abstractj/kalium/).

You must install libsodium before using this application.  If you have homebrew, you can use `brew install libsodium`.

The UserInfoServiceImpl class is where you'll find the symmetric encryption code.

To use the encryption service, add something like this to a controller:

## Controller Usage Example

```scala
@Singleton
class HomeController @Inject()(userInfoService: UserInfoService, cookieBaker: UserInfoCookieBaker) extends Controller {

  def index = Action { implicit request =>
    val optionCookie = request.cookies.get(cookieBaker.COOKIE_NAME)
    optionCookie match {
      case Some(_) =>
        // We can see that the user is a terrible person, and deserves no cake,
        // but the user cannot see the information in the cookie.
        try {
          val userInfo = cookieBaker.decodeFromCookie(optionCookie)
          if (userInfo.terriblePerson) {
            Ok(views.html.index(s"I'm sorry.  All the cake is gone."))
          } else {
            Ok(views.html.index("Hi!  We have cake!"))
          }
        } catch {
          case ex: RuntimeException if (ex.getMessage == "Decryption failed. Ciphertext failed verification") =>
            // This happens if you're in dev mode without a persisted secret and you
            // reload the app server, because a new secret is generated but you still have the
            // old cookie.
            val userInfoCookie = generateUserInfoCookie
            Redirect(routes.HomeController.index()).withCookies(userInfoCookie)
        }
      case None =>
        val userInfoCookie = generateUserInfoCookie
        Redirect(routes.HomeController.index()).withCookies(userInfoCookie)
    }
  }

  private def generateUserInfoCookie: Cookie = {
    // Encode information about the user that we'd rather they not know
    val userInfo = UserInfo(terriblePerson = true)
    val userInfoCookie = cookieBaker.encodeAsCookie(userInfo)
    userInfoCookie
  }
}
```

The CookieBaker will handle encryption and decryption automatically:

```scala
@Singleton
class UserInfoCookieBaker @Inject()(service: UserInfoService) extends CookieBaker[UserInfo] {
  override def COOKIE_NAME: String = "userInfo"

  override def isSigned = false

  override def cookieSigner = { throw new IllegalStateException() }

  override def emptyCookie: UserInfo = new UserInfo()

  override protected def serialize(userInfo: UserInfo): Map[String, String] = service.encrypt(userInfo)

  override protected def deserialize(data: Map[String, String]): UserInfo = service.decrypt(data)
}
```

Then the `UserInfoService` will have the settings:

```scala
trait UserInfoService {
  def decrypt(data: Map[String, String]): UserInfo

  def encrypt(userInfo: UserInfo): Map[String, String]
}

case class UserInfo(terriblePerson: Boolean = false)

object UserInfo {

  // Use a JSON format to automatically convert between case class and JsObject
  implicit val format: OFormat[UserInfo] = Json.format[UserInfo]

}
```

and the actual encryption and decryption are done using SecretBox:

```scala
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
    val secretHex: String = configuration.getString("user.crypto.secret").getOrElse {
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
private[user] class Nonce private[UserInfoServiceImpl](val raw: Array[Byte]) extends AnyVal

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
```

That's it!
