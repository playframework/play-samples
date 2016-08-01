import com.google.inject.AbstractModule
import filters._

/**
 * Provides a base Guice module for setting up some more components from configuration
 * that aren't provided by Play itself.
 */
class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[StrictTransportSecurityConfig]).toProvider(classOf[StrictTransportSecurityConfigProvider])

    install(new post.PostModule)
  }
}




