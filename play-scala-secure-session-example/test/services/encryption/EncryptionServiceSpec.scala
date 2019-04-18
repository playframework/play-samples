package services.encryption

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{Format, Json}

case class Foo(name: String, age: Int)

object Foo {
  implicit val format: Format[Foo] = Json.format[Foo]
}

class EncryptionServiceSpec extends PlaySpec with GuiceOneAppPerTest {

  "encryption info service" should {

    "symmetrically encrypt data" in {
      val service = app.injector.instanceOf(classOf[EncryptionService])
      val secretKey = service.newSecretKey
      val option = Option(Foo(name = "steve", age = 12))
      val encryptedMap = service.encrypt[Foo](secretKey, option)
      val decrypted = service.decrypt[Foo](secretKey, encryptedMap)
      decrypted mustBe Some(Foo(name = "steve", age = 12))
    }

  }

}
