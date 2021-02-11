import java.io.File

import org.scalatestplus.play.FakeApplicationFactory
import play.api._
import play.api.inject._

trait MyApplicationFactory extends FakeApplicationFactory {

  override def fakeApplication(): Application = {
    val env = Environment.simple(new File("."))
    val context = ApplicationLoader.Context.create(env)
    val loader = new MyApplicationLoader()
    loader.load(context)
  }

}
