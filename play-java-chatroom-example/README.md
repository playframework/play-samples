# play-java-chatroom-example

This is a simple chatroom using Play and Websockets with the Java API.

This project makes use of [dynamic streams](https://pekko.apache.org/docs/pekko/current/java/stream/stream-dynamic.html) from Pekko Streams, notably `BroadcastHub` and `MergeHub`.  By [combining MergeHub and BroadcastHub](https://pekko.apache.org/docs/pekko/current/stream/stream-dynamic.html?language=java#dynamic-fan-in-and-fan-out-with-mergehub-broadcasthub-and-partitionhub), you can get publish/subscribe functionality.

## The good bit

The flow is defined once in the controller, and used everywhere from the `chat` action:

```java
public class HomeController extends Controller {

    private final Flow userFlow;

    @Inject
    public HomeController(ActorSystem actorSystem,
                          Materializer mat) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        LoggingAdapter logging = Logging.getLogger(actorSystem.eventStream(), logger.getName());

        //noinspection unchecked
        Source<String, Sink<String, NotUsed>> source = MergeHub.of(String.class)
                .log("source", logging)
                .recoverWithRetries(-1, new PFBuilder().match(Throwable.class, e -> Source.empty()).build());
        Sink<String, Source<String, NotUsed>> sink = BroadcastHub.of(String.class);

        Pair<Sink<String, NotUsed>, Source<String, NotUsed>> sinkSourcePair = source.toMat(sink, Keep.both()).run(mat);
        Sink<String, NotUsed> chatSink = sinkSourcePair.first();
        Source<String, NotUsed> chatSource = sinkSourcePair.second();
        this.userFlow = Flow.fromSinkAndSource(chatSink, chatSource).log("userFlow", logging);
    }

    public Result index() {
        Http.Request request = request();
        String url = routes.HomeController.chat().webSocketURL(request);
        return Results.ok(views.html.index.render(url));
    }

    public WebSocket chat() {
        return WebSocket.Text.acceptOrResult(request -> {
            if (sameOriginCheck(request)) {
                return CompletableFuture.completedFuture(F.Either.Right(userFlow));
            } else {
                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
            }
        });
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

## Tributes

This project is originally taken from Johan Andrén's [Akka-HTTP version](https://github.com/johanandren/chat-with-akka-http-websockets/tree/akka-2.4.10):

Johan also has a blog post explaining dynamic streams in more detail:

* <http://markatta.com/codemonkey/blog/2016/10/02/chat-with-akka-http-websockets/>
