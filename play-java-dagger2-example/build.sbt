lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-dagger2-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      ws,
      "com.google.dagger" % "dagger" % "2.25.2",
      "com.google.dagger" % "dagger-compiler" % "2.25.2"
    ),
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
