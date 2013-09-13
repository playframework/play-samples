package actors

import akka.actor.Actor
import akka.testkit.TestProbe

/**
 * A wrapper around a TestProbe that we can inject into actors.
 */
class ProbeWrapper(probe: TestProbe) extends Actor {
  def receive = {
    case x => probe.ref forward x
  }
}
