lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-java-starter-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.6"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      // Test Database
      "com.h2database" % "h2" % "2.3.232",
      // Testing libraries for dealing with CompletionStage...
      "org.assertj" % "assertj-core" % "3.27.3" % Test,
      "org.awaitility" % "awaitility" % "4.3.0" % Test,
    ),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-parameters",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    // Make verbose tests
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  )
