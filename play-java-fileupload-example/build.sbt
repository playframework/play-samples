lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala3)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-fileupload-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies += guice,
    (Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  Seq("-source:3.0-migration")
        case _              =>  Nil
      }
    }),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
