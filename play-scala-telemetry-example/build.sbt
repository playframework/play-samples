lazy val root = (project in file("."))
  .enablePlugins(PlayScala, Cinnamon)
  .settings(
    name := """play-scala-telemetry-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.12", "3.3.1"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "2.2.224",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  ).settings(
    libraryDependencies ++= Seq(
      Cinnamon.library.cinnamonCHMetrics, // only needed to use the Console reporter
      Cinnamon.library.cinnamonPlay
    )
  )

