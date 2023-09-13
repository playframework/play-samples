
lazy val root = (project in file("."))
  .enablePlugins(PlayJava, Cinnamon)
  .settings(
    name := """play-java-telemetry-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      // Test Database
      "com.h2database" % "h2" % "1.4.200",
      // Testing libraries for dealing with CompletionStage...
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "org.awaitility" % "awaitility" % "4.2.0" % Test,
    ),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-parameters",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    // Make verbose tests
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  ).settings(
    libraryDependencies ++= Seq(
      Cinnamon.library.cinnamonCHMetrics, // only needed to use the Console reporter
      Cinnamon.library.cinnamonPlay
    )
  )
