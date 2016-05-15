# play-websocket-scala

This is an example Play application that shows how to use Play's Websocket API in Scala, by showing a series of stock tickers updated using WebSocket.

The Websocket API is built on Akka Streams, and so is async, non-blocking, and backpressure aware.  Using Akka Streams also means that interacting with Akka Actors is simple.

There are also tests showing how ScalaTest and Akka Testkit are used to test actors and flows.

For more information, please see the documentation for Websockets and Akka Streams:

* https://www.playframework.com/documentation/2.5.x/ScalaWebSockets
* http://doc.akka.io/docs/akka/current/scala/stream/stream-flows-and-basics.html#stream-materialization
* http://doc.akka.io/docs/akka/current/scala/stream/stream-integrations.html#integrating-with-actors
