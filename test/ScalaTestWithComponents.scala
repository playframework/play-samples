import org.scalatest.{Suite, TestData}
import org.scalatestplus.play.{OneAppPerSuite, OneAppPerTest, OneServerPerSuite, OneServerPerTest}
import play.api.{BuiltInComponents, _}

trait OneAppPerTestWithComponents[T <: BuiltInComponents] extends OneAppPerTest with WithContext with WithComponents[T] {
  this: Suite =>

  override def newAppForTest(testData: TestData): Application = createComponents(context).application
}

trait OneAppPerSuiteWithComponents[T <: BuiltInComponents] extends OneAppPerSuite with WithContext with WithComponents[T] {
  this: Suite =>

  lazy val components: T = createComponents(context)

  override implicit lazy val app: Application = components.application
}

trait OneServerPerTestWithComponents[T <: BuiltInComponents] extends OneServerPerTest with WithContext with WithComponents[T] {
  this: Suite =>

  override def newAppForTest(testData: TestData): Application = createComponents(context).application
}

trait OneServerPerSuiteWithComponents[T <: BuiltInComponents] extends OneServerPerSuite with WithContext with WithComponents[T] {
  this: Suite =>

  lazy val components: T = createComponents(context)

  override implicit lazy val app: Application = components.application
}

