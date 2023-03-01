lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-chatroom-example""",
    version := "2.8.x",
    scalaVersion := "2.13.10",
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.8.18",
      "org.webjars" % "flot" % "0.8.3",
      "org.webjars" % "bootstrap" % "5.2.3",
      guice,
      ws,
      "org.assertj" % "assertj-core" % "3.12.2" % Test,
      "org.awaitility" % "awaitility" % "3.1.6" % Test
    ),
    // Needed to make JUnit report the tests being run
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    )
  )
