# play-java-rest-api-example

A REST API showing Play with a JPA backend.  For the Scala version, please see <https://github.com/playframework/play-samples/tree/2.8.x/play-scala-rest-api-example>.

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

## Load Testing

The best way to see what Play can do is to run a load test.  We've included Gatling in this test project for integrated load testing.

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/latest/Deploying) and running the play script:s

```bash
sbt stage
cd target/universal/stage
./bin/play-java-rest-api-example -Dplay.http.secret.key=some-long-key-that-will-be-used-by-your-application
```

Then you'll start the Gatling load test up (it's already integrated into the project):

```bash
sbt ";project;gatling:test"
```

For best results, start the gatling load test up on another machine so you do not have contending resources.  You can edit the [Gatling simulation](http://gatling.io/docs/2.3/general/simulation_structure.html#simulation-structure), and change the numbers as appropriate.

Once the test completes, you'll see an HTML file containing the load test chart, for example:

```bash
 ./play-java-rest-api-example/target/gatling/gatlingspec-1472579540405/index.html
```

That will contain your load test results.
