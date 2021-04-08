lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-dagger2-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.5",
    libraryDependencies ++= Seq(
      ws,
      "com.google.dagger" % "dagger" % "2.25.2",
      "com.google.dagger" % "dagger-compiler" % "2.25.2"
    ),
    // move the java annotation code into generated directory
    javacOptions in Compile := { (managedSourceDirectories in Compile).value.head.mkdirs(); javacOptions.value },
    javacOptions in Compile ++= Seq("-s", (managedSourceDirectories in Compile).value.head.getAbsolutePath),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    // Verbose tests
    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  )
