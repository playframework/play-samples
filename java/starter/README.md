# play-java-starter-example

This is a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

## Running

Run this using [sbt](http://www.scala-sbt.org/).  If you downloaded this project from http://www.playframework.com/download then you'll find a prepackaged version of sbt in the project directory:

```bash
sbt run
```
or
```bash
./gradlew playRun
```

And then go to http://localhost:9000 to see the running web application.

## Controllers

There are several demonstration files available in this template.

- `HomeController.java`:

  Shows how to handle simple HTTP requests.

- `AsyncController.java`:

  Shows how to do asynchronous programming when handling a request.

- `CountController.java`:

  Shows how to inject a component into a controller and use the component when
  handling requests.

## Components

- `Module.java`:

  Shows how to use Guice to bind all the components needed by your application.

- `Counter.java`:

  An example of a component that contains state, in this case a simple counter.

- `ApplicationTimer.java`:

  An example of a component that starts when the application starts and stops
  when the application stops.

## Filters

- `ExampleFilter.java`:

  A simple filter that adds a header to every response.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
