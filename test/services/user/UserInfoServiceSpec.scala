package services.user

import org.scalatestplus.play._

class UserInfoServiceSpec extends PlaySpec with OneAppPerTest {

  "user info service" should {

    "symmetrically encrypt data" in {
      val service = app.injector.instanceOf(classOf[UserInfoServiceImpl])
      val encryptedMap = service.encrypt(UserInfo(terriblePerson = true))
      val decryptedUserInfo = service.decrypt(encryptedMap)
      decryptedUserInfo.terriblePerson mustBe true
    }

  }

}
