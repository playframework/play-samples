lazy val scala213 = "2.13.10"
lazy val scala33 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-dagger2-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala33,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= Seq(
      ws,
      "com.google.dagger" % "dagger" % "2.25.4",
      "com.google.dagger" % "dagger-compiler" % "2.25.4"
    ),
    (Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  Seq("-source:3.0-migration")
        case _              =>  Nil
      }
    }),
    // move the java annotation code into generated directory
    (Compile / javacOptions) := { (Compile / managedSourceDirectories).value.head.mkdirs(); javacOptions.value },
    (Compile / javacOptions) ++= Seq("-s", (Compile / managedSourceDirectories).value.head.getAbsolutePath),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    // Verbose tests
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  )