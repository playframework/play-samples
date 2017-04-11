[<img src="https://img.shields.io/travis/playframework/play-java-rest-api-example.svg"/>](https://travis-ci.org/playframework/play-java-rest-api-example)


# play-java-rest-api-example

A REST API showing Play with a JPA backend.  For the Scala version, please see https://github.com/playframework/play-scala-rest-api-example

## Best Practices for Blocking API

If you look at the controller: https://github.com/playframework/play-java-rest-api-example/blob/master/app/v1/post/PostController.java
then you can see that when calling out to a blocking API like JDBC, you should put it behind an asynchronous boundary -- in practice, this means using the CompletionStage API to make sure that you're not blocking the rendering thread while the database call is going on in the background.

```java
public CompletionStage<Result> list() {
    return handler.find().thenApplyAsync(posts -> {
        final List<PostResource> postList = posts.collect(Collectors.toList());
        return ok(Json.toJson(postList));
    }, ec.current());
}
```

There is more detail in https://www.playframework.com/documentation/latest/ThreadPools -- notably, you can always bump up the number of threads in the rendering thread pool rather than do this -- but it gives you an idea of best practices.
