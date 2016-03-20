import com.google.inject.AbstractModule
import services.creditcard.CreditCardEncryptionService

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[CreditCardEncryptionService]).asEagerSingleton()
  }

}
