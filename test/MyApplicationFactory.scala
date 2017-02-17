import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application

trait MyApplicationFactory extends FakeApplicationFactory {
  override def fakeApplication(): Application = {
    new MyApplicationBuilder().build()
  }
}
