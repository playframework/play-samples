import com.google.inject.AbstractModule
import controllers.UserInfoCookieBaker
import services.user.{UserInfoService, UserInfoServiceImpl}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[UserInfoService]).to(classOf[UserInfoServiceImpl])
    bind(classOf[UserInfoCookieBaker]).asEagerSingleton()
  }

}
