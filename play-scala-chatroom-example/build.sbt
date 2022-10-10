import play.core.PlayVersion.akkaVersion

val playVersion = play.core.PlayVersion.current

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "2.8.x",
    scalaVersion := "2.13.10",
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "2.9.0",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7",
      "net.logstash.logback" % "logstash-logback-encoder" % "6.2",
      "org.jsoup" % "jsoup" % "1.12.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "com.typesafe.play" %% "play-ahc-ws" % playVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
