lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-java-dagger2-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.15", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      ws,
      "com.google.dagger" % "dagger" % "2.52",
      "com.google.dagger" % "dagger-compiler" % "2.52"
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
