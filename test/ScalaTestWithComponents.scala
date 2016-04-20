import org.scalatest.{Suite, TestData}
import org.scalatestplus.play.{OneAppPerSuite, OneAppPerTest, OneServerPerSuite, OneServerPerTest}
import play.api.{BuiltInComponents, _}

trait OneAppPerTestWithComponents[T <: BuiltInComponents]
  extends OneAppPerTest
    with WithApplicationComponents[T] {
  this: Suite =>

  override def newAppForTest(testData: TestData): Application = newApplication
}

trait OneAppPerSuiteWithComponents[T <: BuiltInComponents]
  extends OneAppPerSuite
    with WithApplicationComponents[T] {
  this: Suite =>
  override implicit lazy val app: Application = newApplication
}

trait OneServerPerTestWithComponents[T <: BuiltInComponents]
  extends OneServerPerTest
    with WithApplicationComponents[T] {
  this: Suite =>

  override def newAppForTest(testData: TestData): Application = newApplication
}

trait OneServerPerSuiteWithComponents[T <: BuiltInComponents]
  extends OneServerPerSuite
    with WithApplicationComponents[T] {
  this: Suite =>

  override implicit lazy val app: Application = newApplication
}

