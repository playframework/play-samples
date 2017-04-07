package controllers

import javax.inject.{Inject, Singleton}

import play.api.http.{JWTConfiguration, SecretConfiguration}
import play.api.mvc._
import services.user.{UserInfo, UserInfoService}

import scala.concurrent.duration._

class UserInfoCookieBaker(secretKey: Array[Byte],
                          userInfoService: UserInfoService,
                          val secretConfiguration: SecretConfiguration)
  extends CookieBaker[Option[UserInfo]] with JWTCookieDataCodec {

  private val expirationDate = 365.days

  override val COOKIE_NAME: String = "userInfo"

  override val isSigned = true

  override def emptyCookie: Option[UserInfo] = None

  override val maxAge: Option[Int] = Some(expirationDate.toSeconds.toInt)

  override protected def serialize(userInfo: Option[UserInfo]): Map[String, String] = {
    userInfoService.encrypt(secretKey, userInfo)
  }

  override protected def deserialize(data: Map[String, String]): Option[UserInfo] = {
    userInfoService.decrypt(secretKey, data)
  }

  override val path: String = "/"

  override val jwtConfiguration: JWTConfiguration = JWTConfiguration(expiresAfter = Some(expirationDate))
}

/**
 * Hide the cookie baker dependencies behind a factory
 */
@Singleton
class UserInfoCookieBakerFactory @Inject()(userInfoService: UserInfoService,
                                           secretConfiguration: SecretConfiguration) {

  def createCookieBaker(secretKey: Array[Byte]): UserInfoCookieBaker = {
    new UserInfoCookieBaker(secretKey, userInfoService, secretConfiguration)
  }
}
