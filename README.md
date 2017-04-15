[![Travis](https://img.shields.io/travis/playframework/play-websocket-java.svg?style=flat)](https://travis-ci.org/playframework/play-websocket-java) [![GitHub issues](https://img.shields.io/github/issues/playframework/play-websocket-java.svg?style=flat)](https://github.com/playframework/play-websocket-java/issues) [![GitHub forks](https://img.shields.io/github/forks/playframework/play-websocket-java.svg?style=flat)](https://github.com/playframework/play-websocket-java/network) [![GitHub stars](https://img.shields.io/github/stars/playframework/play-websocket-java.svg?style=flat)](https://github.com/playframework/play-websocket-java/stargazers)

# play-websocket-java-example

This is an example Play application that shows how to use Play's Websocket API in Java, by showing a series of stock tickers updated using WebSocket.

The Websocket API is built on Akka Streams, and so is async, non-blocking, and backpressure aware.  Using Akka Streams also means that interacting with Akka Actors is simple.

There are also tests showing how Junit and Akka Testkit are used to test actors and flows.


## Reactive Push

This application uses a WebSocket to push data to the browser in real-time.  To create a WebSocket connection in Play, first a route must be defined in the <a href="#code/conf/routes" class="shortcut">routes</a> file.  Here is the route which will be used to setup the WebSocket connection:
        
<pre><code>GET /ws controllers.Application.ws</code></pre>

The <code>ws</code> method in the HomeController.java controller handles the request and does the protocol upgrade to the WebSocket connection.  The <code>UserActor</code> stores the handle to the WebSocket connection.<br/>

Once the <code>UserActor</code> is created, the default stocks (defined in `application.conf`) are added to the user's list of watched stocks.<br/>

Each stock symbol has its own <code>StockActor</code> defined in StockActor.java.  This actor holds the last 50 prices for the stock.  Using a <code>FetchHistory</code> message the whole history can be retrieved.  A <code>FetchLatest</code> message will generate a new price.  Every <code>StockActor</code> sends itself a <code>FetchLatest</code> message every 75 milliseconds.  Once a new price is generated it is added to the history and then a message is sent to each <code>UserActor</code> that is watching the stock.  The <code>UserActor</code> then serializes the data as JSON and pushes it to the client using the WebSocket.<br/>

Underneath the covers, resources (threads) are only allocated to the Actors and WebSockets when they are needed.  This is why Reactive Push is scalable with Play and Akka.

## Reactive UI - Real-time Chart

On the client-side, a Reactive UI updates the stock charts every time a message is received.  The <a href="#code/app/views/index.scala.html" class="shortcut">index.scala.html</a> file produces the web page at <a href="http://localhost:9000">http://localhost:9000</a> and loads the JavaScript and CSS needed render the page and setup the UI.<br/>

The JavaScript for the page is compiled from the index.coffee file which is written in CoffeeScript (an elegant way to write JavaScript).  Using jQuery, a page ready handler sets up the WebSocket connection and sets up functions which will be called when the server sends a message to the client through the WebSocket:

<pre><code>$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data</code></pre>

The message is parsed and depending on whether the message contains the stock history or a stock update, a stock chart is either created or updated.  The charts are created using the <strong>Flot</strong> JavaScript charting library.  Using CoffeeScript, jQuery, and Flot makes it easy to build Reactive UI in the browser that can receive WebSocket push events and update the UI in real-time.

## Reactive Requests

When a web server gets a request, it allocates a thread to handle the request and produce a response.  In a typical model the thread is allocated for the entire duration of the request and response, even if the web request is waiting for some other resource.  A Reactive Request is a typical web request and response, but handled in an asynchronous and non-blocking way on the server.  This means that when the thread for a web request is not actively being used, it can be released and reused for something else.

In the Reactive Stocks application the service which determines the stock sentiments is a Reactive Request.  The route is defined in the <a href="#code/conf/routes" class="shortcut">routes</a> file:

<pre><code>GET /sentiment/:symbol controllers.StockSentiment.get(symbol)</code></pre>

A <code>GET</code> request to <code>/sentiment/GOOG</code> will call <code>get("GOOG")</code> on the StockSentiment.java controller.  That method begins with:

<pre><code>def get(symbol: String): Action[AnyContent] = Action.async {</code></pre>

The <code>async</code> block indicates that the controller will return a <code>Future[Result]</code> which is a handle to something that will produce a <code>Result</code> in the future.  The <code>Future</code> provides a way to do asynchronous handling but doesn't necessarily have to be non-blocking.  Often times web requests need to talk to other systems (databases, web services, etc).  If a thread can't be deallocated while waiting for those other systems to respond, then it is blocking.<br/>

In this case a request is made to Twitter and then for each tweet, another request is made to a sentiment service.  All of these requests, including the request from the browser, are all handled as Reactive Requests so that the entire pipeline is Reactive (asynchronous and non-blocking).  This is called Reactive Composition.

## Reactive Composition

Combining multiple Reactive Requests together is Reactive Composition.  The StockSentiment controller does Reactive Composition since it receives a request, makes a request to Twitter for tweets about a stock, and then for each tweet it makes a request to a sentiment service.  All of these requests are Reactive Requests.  None use threads when they are waiting for a response.  Scala's <strong>for comprehensions</strong> make it very easy and elegant to do Reactive Composition.  The basic structure is:

<pre><code>for {
  tweets <- tweetsFuture
  sentiments <- Future.sequence(futuresForTweetSentiment(tweets))
} yield Ok(sentiments)</code></pre>

Because the web client library in Play, <code>WS</code>, is asynchronous and non-blocking, all of the requests needed to get a stock's sentiments are Reactive Requests.  Combined together these Reactive Requests are Reactive Composition.

## Reactive UI - Sentiments

The client-side of Reactive Requests and Reactive Composition is no different than the non-Reactive model.  The browser makes an Ajax request to the server and then calls a JavaScript function when it receives a response.  In the Reactive Stocks application, when a stock chart is flipped over it makes the request for the stock's sentiments.  That is done using jQuery's <code>ajax</code> method in the index.coffee file.  When the request returns data the <code>success</code> handler updates the UI.

## Further Learning

For more information, please see the documentation for Websockets and Akka Streams:

* https://www.playframework.com/documentation/latest/JavaWebSockets
* http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html#stream-materialization
* http://doc.akka.io/docs/akka/current/java/stream/stream-integrations.html#integrating-with-actors
