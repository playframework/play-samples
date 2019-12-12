lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-streaming-example",
    version := "2.8.x",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      ws % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
