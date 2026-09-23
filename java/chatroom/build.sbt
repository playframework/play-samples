resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-java-chatroom-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.3.8"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "3.1.0-M5+4-18ad4fde-SNAPSHOT",
      "org.webjars" % "flot" % "0.8.3",
      "org.webjars" % "bootstrap" % "3.4.1",
      guice,
      ws,
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "org.awaitility" % "awaitility" % "4.2.2" % Test
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    // Needed to make JUnit report the tests being run
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    )
  )
