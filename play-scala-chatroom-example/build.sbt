import play.core.PlayVersion.pekkoVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.15", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "3.0.2",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.3",
      "org.jsoup" % "jsoup" % "1.18.1",
      "ch.qos.logback" % "logback-classic" % "1.5.8",
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % Test,
      "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      //"-deprecation", // gets set by Play automatically
      "-Werror"
    )
  )
