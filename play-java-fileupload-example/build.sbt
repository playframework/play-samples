lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-fileupload-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies += guice,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
