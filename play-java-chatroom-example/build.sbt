lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-chatroom-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "-Xsource:3",
          )
        case _ => Nil
      }
    },
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.9.0-M3",
      "org.webjars" % "flot" % "0.8.3",
      "org.webjars" % "bootstrap" % "3.4.1",
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
