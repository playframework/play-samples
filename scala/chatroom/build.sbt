import play.core.PlayVersion.pekkoVersion

resolvers += Resolver.sonatypeCentralSnapshots

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.17", "3.3.7"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "3.1.0-M1",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      ("net.logstash.logback" % "logstash-logback-encoder" % "8.1")
        .excludeAll(ExclusionRule("com.fasterxml.jackson.core")), // Avoid conflicts with Play's Jackson dependency
      "org.jsoup" % "jsoup" % "1.17.2",
      "ch.qos.logback" % "logback-classic" % "1.5.18",
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % Test,
      "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      //"-deprecation", // gets set by Play automatically
      "-Werror"
    )
  )
