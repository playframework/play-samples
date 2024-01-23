# play-java-compile-di-example

This is a Play project using the Java API and compile time dependency injection.

It is intentionally very simple and basic to show how compile time DI works in Play with the Java API.

There is also an example using Java compile time DI with [Dagger 2](https://google.github.io/dagger/):
the "play-java-dagger2-example" in the [play-samples](https://github.com/playframework/play-samples) repo.

## Running

Start up the server with sbt:

```bash
sbt run
```

Then go to the server at <http://localhost:9000> to see "Hello".

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

