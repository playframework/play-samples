lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-java-fileupload-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("3.9.0", "3.3.8"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies += guice,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
