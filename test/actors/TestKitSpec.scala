package actors

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

/**
 * This class provides any enclosed specs with an ActorSystem and an implicit sender.
 * An ActorSystem can be an expensive thing to set up, so we define a single system
 * that is used for all of the tests.
 */
class TestKitSpec extends TestKit(ActorSystem("testkit"))
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  /**
   * Runs after the example completes.
   */
  override def afterAll {
    TestKit.shutdownActorSystem(system, verifySystemShutdown = true)
  }
}
