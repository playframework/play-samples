# play-java-rest-api-example

A REST API showing Play with a JPA backend.  For the Scala version, please see `play-scala-rest-api-example` in https://github.com/playframework/play-samples/.

## Best Practices for Blocking API

If you look at the controller: [PostController](app/v1/post/PostController.java)
then you can see that when calling out to a blocking API like JDBC, you should put it behind an asynchronous boundary -- in practice, this means using the CompletionStage API to make sure that you're not blocking the rendering thread while the database call is going on in the background.

```java
public CompletionStage<Result> list() {
    return handler.find().thenApplyAsync(posts -> {
        final List<PostResource> postList = posts.collect(Collectors.toList());
        return ok(Json.toJson(postList));
    }, ec.current());
}
```

There is more detail in <https://www.playframework.com/documentation/latest/ThreadPools> -- notably, you can always bump up the number of threads in the rendering thread pool rather than do this -- but it gives you an idea of best practices.

## Play in production mode

The best way to see what Play can do is to run a load test.  We've included Gatling in this test project for integrated load testing.

### Play in production mode (Sbt) 

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/latest/Deploying) and running the next script

```bash
sbt stage
cd target/universal/stage
./bin/play-java-rest-api-example -Dplay.http.secret.key=some-long-key-that-will-be-used-by-your-application
```

### Play in production mode (Gradle)

Start Play in production mode, by [building a distribution](https://docs.gradle.org/current/userguide/application_plugin.html#sec:the_distribution) and running the next script

```bash
./gradlew installDist
cd build/install/play-java-rest-api-example
./bin/play-java-rest-api-example
```

## Load Testing

Then you'll start the Gatling load test up (it's already integrated into the project):

```bash
sbt ";project gatling;gatling:test"
```

For best results, start the gatling load test up on another machine so you do not have contending resources.  You can edit the [Gatling simulation](https://gatling.io/docs/gatling/reference/current/core/simulation/), and change the numbers as appropriate.

Once the test completes, you'll see an HTML file containing the load test chart, for example:

```bash
 ./play-java-rest-api-example/gatling/target/gatling/gatlingspec-1472579540405/index.html
```

That will contain your load test results.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
