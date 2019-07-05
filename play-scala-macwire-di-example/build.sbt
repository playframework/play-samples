lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-macwire-di-example""",
    version := "2.8.x",
    scalaVersion := "2.13.0",
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0-M2" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
