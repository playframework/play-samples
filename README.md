[<img src="https://img.shields.io/travis/playframework/play-java-starter-example.svg"/>](https://travis-ci.org/playframework/play-java-starter-example)

# play-java-starter-example

This is a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

## Preparing

As gRPC requires using HTTP/2 some additional steps have to be taken before you start the project.
Since the project is configured to run with HTTP/2 so we can try out the real end-to-end gRPC interaction,
you will need to change the:

```
play.http.secret.key = ...
``` 

in `application.conf` to different value, as we will be using the `runProd` mode to launch the play app.


## Running

Run this using [sbt](http://www.scala-sbt.org/).  If you downloaded this project from http://www.playframework.com/download then you'll find a prepackaged version of sbt in the project directory:

```
sbt runProd
```

And then go to http://localhost:9000 to see the running web application.
This index page actually performs an gRPC request to the http server, 
