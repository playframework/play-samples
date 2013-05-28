package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._

class UsersActorSpec extends TestkitExample with Specification with NoTimeConversions {

  /*
   * Running tests in parallel (which would ordinarily be the default) will work only if no
   * shared resources are used (e.g. top-level actors with the same name or the
   * system.eventStream).
   *
   * It's usually safer to run the tests sequentially.
   */
  sequential


  "A UsersActor receiving a StockUpdate" should {
    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"
    val price = 123

    "tell all its children about the message" in {
      // Set up a test probe to pass into the actor...
      val probe1 = TestProbe()

      // Create a child actor that points to a test probe
      class UsersActorWithTestProbe extends UsersActor {
        context.actorOf(Props(new ProbeWrapper(probe1)), symbol)
      }

      // create the actor under test
      val actor = system.actorOf(Props(new UsersActorWithTestProbe))

      // send off the stock update...
      val stockUpdate = StockUpdate(symbol, price)
      actor ! stockUpdate

      // Expect the probes to get it.
      val actual = probe1.expectMsg(500 millis, stockUpdate)
      actual must beTheSameAs(stockUpdate)
    }
  }

  "A UsersActor receiving a Listen" should {
    val uuid = java.util.UUID.randomUUID.toString

    "create a new child UserActor" in {
      // no real need for a probe here, but it makes the test happy
      val probe1 = TestProbe()

      var creatorMethodCalled = false
      class UsersActorWithFakeCreator extends UsersActor {
        override def getUserActorCreator(listen:Listen) = new akka.japi.Creator[Actor] {
          def create() = {
            creatorMethodCalled = true
            new ProbeWrapper(probe1)
          }
        }
      }

      // Create the actor under test
      val actor = system.actorOf(Props(new UsersActorWithFakeCreator))

      // Send the Listen...
      val out = new StubOut()
      actor ! Listen(uuid, out)

      // ...and we expect a new child actor.
      creatorMethodCalled must beTrue
    }
  }

  "A UsersActor receiving a WatchStock" should {
    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"

    "tell the child with the UUID about the message" in {
      // Create a UsersActor with a child test probe already injected
      val probe1 = TestProbe()
      class UsersActorWithTestProbe extends UsersActor {
        context.actorOf(Props(new ProbeWrapper(probe1)), uuid)
      }

      val actor = system.actorOf(Props(new UsersActorWithTestProbe))

      val watchStock = WatchStock(uuid, symbol)
      actor ! watchStock

      val actual = probe1.expectMsg(500 millis, watchStock)
      actual must beTheSameAs(watchStock)
    }
  }

  "A UsersActor receiving a UnwatchStock" should {
    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"

    "tell the child with the UUID about the message" in {
      // Create a UsersActor with a child test probe already injected
      val probe1 = TestProbe()
      class UsersActorWithTestProbe extends UsersActor {
        context.actorOf(Props(new ProbeWrapper(probe1)), uuid)
      }
      val actor = system.actorOf(Props(new UsersActorWithTestProbe))

      val unwatchStock = UnwatchStock(uuid, symbol)
      actor ! unwatchStock

      val actual = probe1.expectMsg(500 millis, unwatchStock)
      actual must beTheSameAs(unwatchStock)
    }
  }
}
