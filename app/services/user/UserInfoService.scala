package services.user

import play.api.libs.json.{Json, OFormat}

/**
  * Defines a user info service trait that encrypts and decrypts user infos.
  */
trait UserInfoService {
  def decrypt(data: Map[String, String]): UserInfo

  def encrypt(userInfo: UserInfo): Map[String, String]
}

case class UserInfo(terriblePerson: Boolean = false)

object UserInfo {

  // Use a JSON format to automatically convert between case class and JsObject
  implicit val format: OFormat[UserInfo] = Json.format[UserInfo]

}
