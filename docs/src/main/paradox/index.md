# Making a REST API with Play

This is a multi-part guide to walk you through how to make a RESTful API with JSON using [Play 2.5](https://playframework.com).

We’ll demonstrate with a “best practices” REST API.  You can get source code for this guide two ways:

* Download a pre-packaged bundle with this link [https://example.lightbend.com/v1/download/play-scala-rest-api-example](https://example.lightbend.com/v1/download/play-scala-rest-api-example)

    * Linux/Mac:
```bash
unzip play-scala-rest-api-example.zip
cd play-scala-rest-api-example
./sbt
```
    * Windows:

        1. Unzip the download
        2. From a command line `cd` into the directory where you expanded the downloaded `zip` file and run:
```
sbt.bat
```

* [From Github](https://github.com/playframework/play-scala-rest-api-example/tree/2.5.x):
```
git clone https://github.com/playframework/play-scala-rest-api-example.git
git checkout 2.5.x
```

This example is in Scala, but Play also has a [Java API](https://www.playframework.com/documentation/2.5.x/JavaHome) which looks and acts just like the [Scala API](https://www.playframework.com/documentation/2.5.x/ScalaHome), and has a corresponding [play-java-rest-api-example](https://github.com/playframework/play-java-rest-api-example) project.  For instructions on running and using the project, please see the [[appendix]].  This project also comes with an integrated [Gatling](http://gatling.io/) load test -- again, instructions are in the appendix.

Note that there’s more involved in a REST API -- monitoring, representation, and managing access to back end resources -- that we'll cover in subsequent posts.  But first, let's address why Play is so effective as a REST API.

## When to use Play

Play makes a good REST API implementation because Play does the right thing out of the box.  Play makes simple things easy, makes hard things possible, and encourages code that scales because it works in sympathy with the JVM and the underlying hardware. But "safe and does the right thing" is the boring answer.

The fun answer is that [Play is **fast**](https://www.lightbend.com/blog/why-is-play-framework-so-fast).

In fact, Play is so fast that you have to turn off machines so that the rest of your architecture can keep up.  The Hootsuite team was able to **reduce the number of servers by 80%** by [switching to Play](https://www.lightbend.com/resources/case-studies-and-stories/how-hootsuite-modernized-its-url-shortener).  if you deploy Play with the same infrastructure that you were using for other web frameworks, you are effectively staging a denial of service attack against your own database.

Play is fast because Play is **built on reactive bedrock**.  Play starts from a reactive core, and builds on reactive principles all the way from the ground.  Play breaks network packets into a stream of small chunks of bytes.  It keeps a small pool of work stealing threads, mapped to the number of cores in the machine, and keeps those threads fed with those chunks.  Play exposes those byte chunks to the application for body parsing, Server Sent Events and WebSockets through [Akka Streams](http://doc.akka.io/docs/akka/2.4/scala/stream/stream-introduction.html) -- the Reactive Streams implementation designed by the people who invented [Reactive Streams](http://www.reactive-streams.org/) and wrote the [Reactive Manifesto](http://www.reactivemanifesto.org/).

Linkedin uses Play throughout its infrastructure. It wins on all [four quadrants of scalability](http://www.slideshare.net/brikis98/the-play-framework-at-linkedin/128-Outline1_Getting_started_with_Play2) ([video](https://youtu.be/8z3h4Uv9YbE)).  Play's average "request per second" comes in around [tens of k on a basic quad core w/o any intentional tuning](https://twitter.com/kevinbowling1/status/764188720140398592) -- and it only gets better.

Play provides an easy to use MVC paradigm, including hot-reloading without any JVM bytecode magic or container overhead.  Startup time for a developer on Play was **reduced by roughly 7 times** for [Walmart Canada](https://www.lightbend.com/resources/case-studies-and-stories/walmart-boosts-conversions-by-20-with-lightbend-reactive-platform), and using Play **reduced development times by 2x to 3x**.

Play combines this with a **reactive programming API** that lets you write async, non-blocking code in a straightforward fashion without worrying about complex and confusing "callback hell."  In both Java or Scala, Play works on the same principle: leverage the asynchronous computation API that the language provides to you.  In Play, you work with [`java.util.concurrent.CompletionStage`](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/changes8.html) or [`scala.concurrent.Future`](http://docs.scala-lang.org/overviews/core/futures.html) API directly, and Play passes that asynchronous computation back through the framework.

Finally, Play is modular and extensible.  Play works with multiple runtime and compile time dependency injection frameworks like [Guice](https://www.playframework.com/documentation/2.5.x/ScalaDependencyInjection), [Macwire](https://di-in-scala.github.io/), [Dagger](https://github.com/esfand-r/play-java-dagger-dependency-injection#master), and leverages DI principles to integrate authentication and authorization frameworks built on top of Play.

## Community

To learn more about Play, check out the [Play tutorials](https://playframework.com/documentation/2.5.x/Tutorials) and see more examples and blog posts about Play, including streaming [Server Side Events](https://github.com/playframework/play-streaming-scala) and first class [WebSocket support](https://github.com/playframework/play-websocket-scala).

To get more involved and if you have questions, join the [mailing list](https://groups.google.com/forum/#!forum/play-framework) at  and follow [PlayFramework on Twitter](https://twitter.com/playframework).

## Microservices vs REST APIs

One thing to note here is that although this guide covers how to make a REST API in Play, it only covers Play itself and deploying Play.  Building a REST API in Play does not automatically make it a "microservice" because it does not cover larger scale concerns about microservices such as ensuring resiliency, consistency, or monitoring.

For full scale microservices, you want [Lagom](http://www.lagomframework.com/), which builds on top of Play -- a microservices framework for dealing with the ["data on the outside"](https://blog.acolyer.org/2016/09/13/data-on-the-outside-versus-data-on-the-inside/) problem, set up with persistence and service APIs that ensure that the service always stays up and responsive even in the face of chaos monkeys and network partitions.

With that caveat, let's start working with Play!

@@@index

* [Basics](part-1/index.md)
* [Appendix](appendix.md)

@@@
