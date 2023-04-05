lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  .settings(
    name := "play-java-ebean-example",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "com.h2database" % "h2" % "2.1.214",
      "org.awaitility" % "awaitility" % "3.1.6" % Test,
      "org.assertj" % "assertj-core" % "3.12.2" % Test,
      "org.mockito" % "mockito-core" % "3.0.0" % Test,
    ),
    (Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  Seq("-source:3.0-migration")
        case _              =>  Nil
      }
    }),
    (Test / testOptions) += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
