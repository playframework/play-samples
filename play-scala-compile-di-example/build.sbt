lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    scalacOptions ++= List(
      "-encoding", "utf8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings"
    )
  )
