import play.core.PlayVersion.pekkoVersion

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.4"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "3.1.0-M1",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
      "org.jsoup" % "jsoup" % "1.17.2",
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % Test,
      "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M1" % Test
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      //"-deprecation", // gets set by Play automatically
      "-Werror"
    )
  )
