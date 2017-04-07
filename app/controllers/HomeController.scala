package controllers

import javax.inject._

import play.api.i18n.{Lang, Messages, MessagesApi, MessagesProvider}
import play.api.mvc._
import services.session.SessionService
import services.user.{UserInfo, UserInfoService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(userAction: UserInfoAction,
                               sessionService: SessionService,
                               userInfoService: UserInfoService,
                               cc: ControllerComponents) extends AbstractController(cc) {

  import UserInfoForm._

  def index = userAction { implicit request: UserRequest[AnyContent] =>
    Ok(views.html.index(form))
  }

}

object UserInfoForm {

  import play.api.data.Form
  import play.api.data.Forms._

  val form = Form(
    mapping(
      "username" -> text
    )(UserInfo.apply)(UserInfo.unapply)
  )

}

object CookieStripper {
  def logout(result: Result): Result = {
    result.withNewSession.discardingCookies(DiscardingCookie("userInfo"))
  }
}

class UserRequest[A](request: Request[A], val userInfo: Option[UserInfo], messagesApi: MessagesApi)
  extends FormAwareWrappedRequest[A](request, messagesApi)


abstract class FormAwareWrappedRequest[A](request: Request[A], messagesApi: MessagesApi)
  extends WrappedRequest[A](request) with MessagesProvider {
  lazy val messages: Messages = messagesApi.preferred(request)
  lazy val lang: Lang = messages.lang
}

/**
 * An action that pulls everything together to show user info that is in an encrypted cookie,
 * with only the secret key stored on the server.
 */
@Singleton
class UserInfoAction @Inject()(sessionService: SessionService,
                               factory: UserInfoCookieBakerFactory,
                               playBodyParsers: PlayBodyParsers,
                               messagesApi: MessagesApi,
                               ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = playBodyParsers.anyContent
  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    block(new UserRequest[A](request, userInfoFromRequest(request), messagesApi))
  }

  private def userInfoFromRequest(request: RequestHeader): Option[UserInfo] = {
    val maybeCookieBaker = for {
      sessionId <- request.session.get("sessionId")
      secretKey <- sessionService.lookup(sessionId)
    } yield factory.createCookieBaker(secretKey)

    maybeCookieBaker.flatMap { cookieBaker =>
      cookieBaker.decodeFromCookie(request.cookies.get(cookieBaker.COOKIE_NAME))
    }
  }

}
