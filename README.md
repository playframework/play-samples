# Play using Log4j 2

This is an example project showing a sample Play application that use Log4J 2 instead of using Logback.

Please see [Using a custom logging framework](https://www.playframework.com/documentation/2.6.x/SettingsLogger#Using-a-Custom-Logging-Framework) in the Play documentation for more details.

## Running in Production

This application will package everything correctly when you run `sbt dist` and run the packaged script.

There is an outstanding bug where apparently this didn't work: please add comments to <https://github.com/playframework/playframework/issues/6017> if this doesn't work for you.

## Running in Development

You must define the `log4j.configurationFile` explicitly when the JVM is loaded or `sbt`:

```bash
sbt -Dlog4j.configurationFile=conf/log4j2.xml
```

If you do not run with `log4j.configurationFile` loaded, you will see this error:

```log
ERROR StatusLogger No log4j2 configuration file found. Using default configuration: logging only errors to the console.
```

After you define the log4j system property, running the application should look like this:

```log
[info] Loading project definition from /Users/wsargent/work/play-2.5-log4j2/project
[info] Set current project to play-2.5-log4j2 (in build file:/Users/wsargent/work/play-2.5-log4j2/)
[play-2.5-log4j2] $ run

No play.logger.configurator found: logging must be configured entirely by the application.
--- (Running the application, auto-reloading is enabled) ---

[INFO ] 2016-03-29 13:09:05.265 [pool-6-thread-2] NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Ctrl+D to stop and go back to the console...)

[info] Compiling 1 Scala source to /Users/wsargent/work/play-2.5-log4j2/target/scala-2.11/classes...
[INFO ] 2016-03-29 13:09:14.051 [ForkJoinPool-1-worker-1] application - ApplicationTimer demo: Starting application at 2016-03-29T20:09:14.031Z.
[INFO ] 2016-03-29 13:09:14.279 [application-akka.actor.default-dispatcher-2] Slf4jLogger - Slf4jLogger started
[INFO ] 2016-03-29 13:09:14.307 [ForkJoinPool-1-worker-1] Play - Application started (Dev)
````

Note that you will see

```log
No play.logger.configurator found: logging must be configured entirely by the application.
```

when you first start it -- this is a side effect of Play's immediate reload functionality, and will not affect the application itself.  You won't see the `play.logger.configurator`  warning if you run the Play application in production, because there isn't a different class loader for SBT vs for the Play application.
