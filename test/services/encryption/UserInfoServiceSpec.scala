package services.encryption

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import services.user.{UserInfo, UserInfoService}

class UserInfoServiceSpec extends PlaySpec with GuiceOneAppPerTest {

  "encryption info service" should {

    "symmetrically encrypt data" in {
      val service = app.injector.instanceOf(classOf[UserInfoService])
      val secretKey = service.newSecretKey
      val encryptedMap = service.encrypt(secretKey, Option(UserInfo(username = "will")))
      val decryptedUserInfo = service.decrypt(secretKey, encryptedMap)
      decryptedUserInfo mustBe Some(UserInfo("will"))
    }

  }

}
