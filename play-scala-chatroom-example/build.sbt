import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.14", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "2.9.1",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.3",
      "org.jsoup" % "jsoup" % "1.17.2",
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.1" % Test
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      //"-deprecation", // gets set by Play automatically
      "-Werror"
    )
  )
