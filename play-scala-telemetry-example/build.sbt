lazy val root = (project in file("."))
  .enablePlugins(PlayScala, Cinnamon)
  .settings(
    name := """play-scala-telemetry-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "1.4.200",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  ).settings(
    libraryDependencies ++= Seq(
      Cinnamon.library.cinnamonCHMetrics, // only needed to use the Console reporter
      Cinnamon.library.cinnamonPlay
    )
  )

