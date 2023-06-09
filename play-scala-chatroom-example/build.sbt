import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.11",
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "2.8.18",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
      "org.jsoup" % "jsoup" % "1.16.1",
      "ch.qos.logback" % "logback-classic" % "1.2.12",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
