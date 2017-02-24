package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.CookieBaker
import services.user.{UserInfo, UserInfoService}

@Singleton
class UserInfoCookieBaker @Inject()(service: UserInfoService) extends CookieBaker[UserInfo] {
  override val COOKIE_NAME: String = "userInfo"

  override val isSigned = false

  override def cookieSigner = { throw new IllegalStateException() }

  override def emptyCookie: UserInfo = new UserInfo()

  override protected def serialize(userInfo: UserInfo): Map[String, String] = service.encrypt(userInfo)

  override protected def deserialize(data: Map[String, String]): UserInfo = service.decrypt(data)

  override val path: String = "/"
}
