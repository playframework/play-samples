package controllers

import javax.inject.{Inject, Singleton}

import play.api.http.{JWTConfiguration, SecretConfiguration}
import play.api.mvc._
import services.user.{UserInfo, UserInfoService}

@Singleton
class UserInfoCookieBaker @Inject()(service: UserInfoService,
                                    val secretConfiguration: SecretConfiguration)
  extends CookieBaker[UserInfo] with JWTCookieDataCodec {

  override val COOKIE_NAME: String = "userInfo"

  override val isSigned = true

  override def emptyCookie: UserInfo = new UserInfo()

  override protected def serialize(userInfo: UserInfo): Map[String, String] = service.encrypt(userInfo)

  override protected def deserialize(data: Map[String, String]): UserInfo = service.decrypt(data)

  override val path: String = "/"

  override val jwtConfiguration: JWTConfiguration = JWTConfiguration()
}
