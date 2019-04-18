package services.session

import javax.inject.Inject

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
 * Start up Akka cluster nodes on different ports in the same JVM for
 * the distributing caching.
 *
 * Normally you'd run several play instances, and the port would be the
 * same while you had several different ip addresses.
 */
class ClusterSystem @Inject() (configuration: Configuration, applicationLifecycle: ApplicationLifecycle) {
  private val systems = startup(Seq("2551", "2552"))

  def startup(ports: Seq[String]): Seq[ActorSystem] = {
    ports.map { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString(
        s"""akka.remote.artery.canonical.port = $port"""
      ).withFallback(configuration.underlying)

      // use the same name as Play's application actor system, because these are
      // supposed to be "remote" play instances all sharing a distribute cache
      ActorSystem(config.getString("play.akka.actor-system"), config)
    }
  }

  applicationLifecycle.addStopHook { () =>
    Future.successful(systems.foreach(_.terminate()))
  }
}
