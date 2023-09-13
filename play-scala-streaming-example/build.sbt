lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-streaming-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      ws % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
