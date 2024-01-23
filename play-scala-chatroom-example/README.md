# play-scala-chatroom-example

This is a simple chatroom using Play and Websockets with the Scala API.

This project makes use of [dynamic streams](https://pekko.apache.org/docs/pekko/current/scala/stream/stream-dynamic.html) from Pekko Streams, notably `BroadcastHub` and `MergeHub`.  By [combining MergeHub and BroadcastHub](https://pekko.apache.org/docs/pekko/current/stream/stream-dynamic.html?language=scala#dynamic-fan-in-and-fan-out-with-mergehub-broadcasthub-and-partitionhub), you can get publish/subscribe functionality.

## The good bit

The flow is defined once in the controller, and used everywhere from the `chat` action:

```scala
import javax.inject._
import play.api.mvc._

import org.apache.pekko.stream.scaladsl._
import scala.concurrent._

class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

    private type WSMessage = String

  // chat room many clients -> merge hub -> broadcasthub -> many clients
  private val (chatSink, chatSource) = {

    // Don't log MergeHub$ProducerFailed as error if the client disconnects.
    // recoverWithRetries -1 is essentially "recoverWith"
    val source = MergeHub.source[WSMessage]
      .log("source")
      .recoverWithRetries(-1, { case _: Exception => Source.empty })

    val sink = BroadcastHub.sink[WSMessage]
    source.toMat(sink)(Keep.both).run()
  }

  private val userFlow: Flow[WSMessage, WSMessage, _] = {
    Flow[WSMessage].via(Flow.fromSinkAndSource(chatSink, chatSource)).log("userFlow")
  }

  def chat: WebSocket = {
    WebSocket.acceptOrResult[WSMessage, WSMessage] {
      case rh if sameOriginCheck(rh) =>
        Future.successful(userFlow).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            Left(InternalServerError("Cannot create websocket"))
        }

      case rejected =>
        Future.successful {
          Left(Forbidden("forbidden"))
        }
    }
  }
}
```

## Prerequisites

You will need [JDK 11](https://adoptopenjdk.net/) and [sbt](http://www.scala-sbt.org/) installed.

## Running

```bash
sbt run
```

Go to <http://localhost:9000> and open it in two different browsers.  Typing into one browser will cause it to show up in another browser.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Tributes

This project is originally taken from Johan Andrén's [Akka-HTTP version](https://github.com/johanandren/chat-with-akka-http-websockets/tree/akka-2.4.10):

Johan also has a blog post explaining dynamic streams in more detail:

* <http://markatta.com/codemonkey/blog/2016/10/02/chat-with-akka-http-websockets/>
