# Play Hello World Web Tutorial for Scala

To follow the steps in this tutorial, you will need the correct version of Java and sbt. The template requires:

* Java Software Developer's Kit (SE) 1.8 or higher
* sbt 0.13.15 or higher (we recommend 1.2.3) Note: if you downloaded this project as a zip file from https://developer.lightbend.com, the file includes an sbt distribution for your convenience.

To check your Java version, enter the following in a command window:

```bash
java -version
```

To check your sbt version, enter the following in a command window:

```bash
sbt sbt-version
```

If you do not have the required versions, follow these links to obtain them:

* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [sbt](http://www.scala-sbt.org/download.html)

## Build and run the project

This example Play project was created from a seed template. It includes all Play components and an Akka HTTP server. The project is also configured with filters for Cross-Site Request Forgery (CSRF) protection and security headers.

To build and run the project:

1. Use a command window to change into the example project directory, for example: `cd play-scala-hello-world-web`

2. Build the project. Enter: `sbt run`. The project builds and starts the embedded HTTP server. Since this downloads libraries and dependencies, the amount of time required depends partly on your connection's speed.

3. After the message `Server started, ...` displays, enter the following URL in a browser: <http://localhost:9000>

The Play application responds: `Welcome to the Hello World Tutorial!`
