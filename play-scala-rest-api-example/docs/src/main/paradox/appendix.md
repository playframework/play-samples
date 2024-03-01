
# Appendix

This appendix covers how to download, run, use and load test Play.

## Requirements

You will need JDK 11 which can download from [here](https://adoptopenjdk.net/).

You will need to have [git](https://git-scm.com/) installed.

## Downloading

You can clone the example project from GitHub:

```bash
git clone https://github.com/playframework/play-samples.git
cd play-samples/play-scala-rest-api-example
```

## Running

You need to download and install sbt for this application to run.  You can do that by going to the [sbt download page](http://www.scala-sbt.org/download.html) and following the instructions for your platform.

Once you have sbt installed, the following at the command prompt will download any required library dependencies, and start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:9000/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.  You can read more about using Play [here](https://www.playframework.com/documentation/latest/PlayConsole).

## Usage

If you call the same URL from the command line, you’ll see JSON. Using [httpie](https://httpie.org/), we can execute the command:

```bash
http --verbose http://localhost:9000/v1/posts
```

And get back:

```
GET /v1/posts HTTP/1.1
```

Likewise, you can also send a POST directly as JSON:

```bash
http --verbose POST http://localhost:9000/v1/posts title="hello" body="world"
```

and get:

```
POST /v1/posts HTTP/1.1
```

## Load Testing

The best way to see what Play can do is to run a load test.  We've included [Gatling](https://gatling.io/) in this test project for integrated load testing.

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/latest/Deploying) and running the play scripts:

```bash
sbt stage
./target/universal/stage/bin/play-scala-rest-api-example -Dplay.http.secret.key=testing
```

Then you'll start the Gatling load test up (it's already integrated into the project):

```bash
sbt ";project;gatling:test"
```

For best results, start the gatling load test up on another machine so you do not have contending resources.  You can edit the [Gatling simulation](https://gatling.io/docs/gatling/reference/current/core/simulation/), and change the numbers as appropriate.

Once the test completes, you'll see an HTML file containing the load test chart:

```bash
./play-scala-rest-api-example/target/gatling/gatlingspec-1472579540405/index.html
```

That will contain your load test results.
