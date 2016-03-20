# Play Kalium

This is an example application that shows how to use symmetric encryption with [Kalium](https://github.com/abstractj/kalium/).

You must install libsodium before using this application.  If you have homebrew, you can use `brew install libsodium`.

The UserInfoServiceImpl class is where you'll find the symmetric encryption code.

To use the encryption service, add something like this to a controller:

## Controller Usage Example

```scala
@Singleton
class HomeController @Inject()(userInfoService: UserInfoService, cookieBaker: UserInfoCookieBaker) extends Controller {

  def index = Action { implicit request =>
    val optionCookie = request.cookies.get(cookieBaker.COOKIE_NAME)
    optionCookie match {
      case Some(_) =>
        // We can see that the user is a terrible person, and deserves no cake,
        // but the user cannot see the information in the cookie.
        try {
          val userInfo = cookieBaker.decodeFromCookie(optionCookie)
          if (userInfo.terriblePerson) {
            Ok(views.html.index(s"I'm sorry.  All the cake is gone."))
          } else {
            Ok(views.html.index("Hi!  We have cake!"))
          }
        } catch {
          case ex: RuntimeException if (ex.getMessage == "Decryption failed. Ciphertext failed verification") =>
            // This happens if you're in dev mode without a persisted secret and you
            // reload the app server, because a new secret is generated but you still have the
            // old cookie.
            val userInfoCookie = generateUserInfoCookie
            Redirect(routes.HomeController.index()).withCookies(userInfoCookie)
        }
      case None =>
        val userInfoCookie = generateUserInfoCookie
        Redirect(routes.HomeController.index()).withCookies(userInfoCookie)
    }
  }

  private def generateUserInfoCookie: Cookie = {
    // Encode information about the user that we'd rather they not know
    val userInfo = UserInfo(terriblePerson = true)
    val userInfoCookie = cookieBaker.encodeAsCookie(userInfo)
    userInfoCookie
  }
}
```

That's it.
