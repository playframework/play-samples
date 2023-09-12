lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := "play-java-websocket-example",
    version := "1.0",
    scalaVersion := "2.13.12",
    // https://github.com/sbt/junit-interface
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" %% "webjars-play" % "2.8.18",
      "org.webjars" % "bootstrap" % "2.3.2",
      "org.webjars" % "flot" % "0.8.3",

      // Testing libraries for dealing with CompletionStage...
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "org.awaitility" % "awaitility" % "4.2.0" % Test,
    ),
    LessKeys.compress := true,
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    )
  )
