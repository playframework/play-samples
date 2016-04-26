import org.scalatest.Suite
import play.api.ApplicationLoader.Context

trait OneAppPerTestWithMyComponents
  extends OneAppPerTestWithComponents[MyComponents] {
  this: Suite =>

  override def createComponents(context: Context): MyComponents = new MyComponents(context)
}

trait OneAppPerSuiteWithMyComponents
  extends OneAppPerSuiteWithComponents[MyComponents] {
  this: Suite =>

  override def createComponents(context: Context): MyComponents = new MyComponents(context)
}

trait OneServerPerTestWithMyComponents
  extends OneServerPerTestWithComponents[MyComponents] {
  this: Suite =>

  override def createComponents(context: Context): MyComponents = new MyComponents(context)
}

trait OneServerPerSuiteWithMyComponents
  extends OneServerPerSuiteWithComponents[MyComponents] {
  this: Suite =>

  override def createComponents(context: Context): MyComponents = new MyComponents(context)
}
