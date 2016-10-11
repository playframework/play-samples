# Play Chatroom

This is a simple chatroom using Play and Websockets with the Scala API.

This project makes use of [dynamic streams](http://doc.akka.io/docs/akka/current/scala/stream/stream-dynamic.html) from Akka Streams, notably `BroadcastHub` and `MergeHub`.  By [combining MergeHub and BroadcastHub](http://doc.akka.io/docs/akka/current/scala/stream/stream-dynamic.html#Dynamic_fan-in_and_fan-out_with_MergeHub_and_BroadcastHub), you can get publish/subscribe functionality.

## The good bit

The flow is defined once in the controller, and used everywhere from the `chat` action:

```scala
class HomeController extends Controller {

  // chat room many clients -> merge hub -> broadcasthub -> many clients
  private val (chatSink, chatSource) = {

    // Don't log MergeHub$ProducerFailed as error if the client disconnects.
    // recoverWithRetries -1 is essentially "recoverWith"
    val source = MergeHub.source[WSMessage]
      .log("source")
      .recoverWithRetries(-1, { case _: Exception ⇒ Source.empty })

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
            val msg = "Cannot create websocket"
            logger.error(msg, e)
            val result = InternalServerError(msg)
            Left(result)
        }

      case rejected =>
        logger.error(s"Request ${rejected} failed same origin check")
        Future.successful {
          Left(Forbidden("forbidden"))
        }
    }
  }
}
```

## Prerequisites

You will need [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [sbt](http://www.scala-sbt.org/) installed.

## Running

```
sbt run
```

Go to http://localhost:9000 and open it in two different browsers.  Typing into one browser will cause it to show up in another browser.

## Tributes

This project is originally taken from Johan Andrén's [Akka-HTTP version](https://github.com/johanandren/chat-with-akka-http-websockets/tree/akka-2.4.10):

Johan also has a blog post explaining dynamic streams in more detail:

* http://markatta.com/codemonkey/blog/2016/10/02/chat-with-akka-http-websockets/

