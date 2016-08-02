
# Appendix

This appendix covers how to download, run, use and load test Play.

## Requirements

You will need a JDK 1.8 that is more recent than b20.  You can download the JDK from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

You will need to have git installed.

## Downloading

You can download the example project from Github:

```
git clone https://github.com/playframework/play-rest-api.git
```

## Running

You need to download and install sbt for this application to run.  You can do that by going to the [sbt download page](http://www.scala-sbt.org/download.html) and following the instructions for your platform.

Once you have sbt installed, the following at the command prompt will download any required library dependencies, and start up Play in development mode:

```
sbt run
```

Play will start up on the HTTP port at http://localhost:9000/.   You don't need to reploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.  You can read more about using Play [here](https://www.playframework.com/documentation/2.5.x/PlayConsole).

## Usage

If you call the same URL from the command line, you’ll see JSON. Using [httpie](https://httpie.org/), we can execute the command:

```
http --verbose http://localhost:9000/v1/posts
```

and get back:

```
GET /v1/posts HTTP/1.1
```

Likewise, you can also send a POST directly as JSON:

```
http --verbose POST http://localhost:9000/v1/posts title="hello" body="world"
```

and get:

```
POST /v1/posts HTTP/1.1
```

## Load Testing

The best way to see what Play can do is to run a load test.  We've included Gatling in this test project for integrated load testing.

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/2.5.x/Deploying) and running the play scripts:

```
sbt stage
cd target/universal/stage
bin/play-rest-api -Dplay.crypto.secret=testing
```

Then you'll start the Gatling load test up (it's already integrated into the project):

```
sbt gatling:test
```

For best results, start the gatling load test up on another machine so you do not have contending resources.  You can edit the [Gatling simulation](http://gatling.io/docs/2.2.2/general/simulation_structure.html#simulation-structure), and change the numbers as appropriate.

Once the test completes, you'll see an HTML file containing the load test chart:

```
 ./rest-api/target/gatling/gatlingspec-1472579540405/index.html
```

That will contain your load test results.
