import java.time.Clock
import javax.inject.{Inject, Singleton}

import play.api.http.SecretConfiguration
import play.api.i18n.{Messages, MessagesApi, MessagesProvider}
import play.api.libs.json.{Format, Json}
import play.api.mvc._
import services.encryption.{EncryptedCookieBaker, EncryptionService}
import services.session.SessionService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Methods and objects common to all controllers
 */
package object controllers {

  import play.api.data.Form
  import play.api.data.Forms._

  val SESSION_ID = "sessionId"

  val FLASH_ERROR = "error"

  val USER_INFO_COOKIE_NAME = "userInfo"

  case class UserInfo(username: String)

  object UserInfo {
    // Use a JSON format to automatically convert between case class and JsObject
    implicit val format: Format[UserInfo] = Json.format[UserInfo]
  }

  val form = Form(
    mapping(
      "username" -> text
    )(UserInfo.apply)(UserInfo.unapply)
  )

  def discardingSession(result: Result): Result = {
    result.withNewSession.discardingCookies(DiscardingCookie(USER_INFO_COOKIE_NAME))
  }

  /**
   * An action that pulls everything together to show user info that is in an encrypted cookie,
   * with only the secret key stored on the server.
   */
  @Singleton
  class UserInfoAction @Inject()(sessionService: SessionService,
                                 factory: UserInfoCookieBakerFactory,
                                 playBodyParsers: PlayBodyParsers,
                                 messagesApi: MessagesApi
                                 )(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[UserRequest, AnyContent] {

    private val clock = Clock.systemUTC()

    override def parser: BodyParser[AnyContent] = playBodyParsers.anyContent

    override def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
      userRequestFromRequest(request).flatMap(block)
    }

    private def userRequestFromRequest[A](request: Request[A]): Future[UserRequest[A]] = {
      userInfoFromRequest(request).map { maybeUserInfo =>
        new UserRequest[A](request, maybeUserInfo, messagesApi)
      }
    }

    private def userInfoFromRequest(request: RequestHeader): Future[Option[UserInfo]] = {
      val futureMaybeSessionId = request.session.get(SESSION_ID).map(sessionService.lookup).getOrElse(Future.successful(None))
      val futureMaybeCookieBaker = futureMaybeSessionId.map(_.map(factory.createCookieBaker))
      futureMaybeCookieBaker.map(_.flatMap(_.decodeFromCookie(request.cookies.get(USER_INFO_COOKIE_NAME))))
    }
  }

  // Minimum work needed to avoid using I18nController
  trait MessagesRequestHeader extends MessagesProvider { self: RequestHeader =>
    def messagesApi: MessagesApi
    lazy val messages: Messages = messagesApi.preferred(self)
  }

  class UserRequest[A](request: Request[A],
                       val userInfo: Option[UserInfo],
                       val messagesApi: MessagesApi)
    extends WrappedRequest[A](request) with MessagesRequestHeader

  /**
   * Creates a cookie baker with the given secret key.
   */
  @Singleton
  class UserInfoCookieBakerFactory @Inject()(encryptionService: EncryptionService,
                                             secretConfiguration: SecretConfiguration) {

    def createCookieBaker(secretKey: Array[Byte]): EncryptedCookieBaker[UserInfo] = {
      new EncryptedCookieBaker[UserInfo](secretKey, encryptionService, secretConfiguration) {
        // This can also be set to the session expiration, but lets keep it around for example
        override val expirationDate: FiniteDuration = 365.days
        override val COOKIE_NAME: String = USER_INFO_COOKIE_NAME
      }
    }
  }

  @Singleton
  class SessionGenerator @Inject()(
    sessionService: SessionService,
    userInfoService: EncryptionService,
    factory: UserInfoCookieBakerFactory
  )(implicit ec: ExecutionContext) {

    def createSession(userInfo: UserInfo): Future[(String, Cookie)] = {
      // create a user info cookie with this specific secret key
      val secretKey = userInfoService.newSecretKey
      val cookieBaker = factory.createCookieBaker(secretKey)
      val userInfoCookie = cookieBaker.encodeAsCookie(Some(userInfo))

      // Tie the secret key to a session id, and store the encrypted data in client side cookie
      sessionService.create(secretKey).map(sessionId => (sessionId, userInfoCookie))
    }

  }

}
