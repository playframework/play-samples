lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-secure-session-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.0",
    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.abstractj.kalium" % "kalium" % "0.8.0",
      "com.typesafe.akka" %% "akka-distributed-data" % "2.6.0-M3",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0-M3" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
