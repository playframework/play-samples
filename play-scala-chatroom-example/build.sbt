import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.11", "3.3.0"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "2.9.0-M3",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "6.6",
      "org.jsoup" % "jsoup" % "1.16.1",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M3" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      //"-deprecation", // gets set by Play automatically
      "-Werror"
    )
  )
