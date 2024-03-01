lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-java-ebean-example",
    version := "1.0.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.13", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "com.h2database" % "h2" % "2.2.224",
      "org.awaitility" % "awaitility" % "4.2.0" % Test,
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "org.mockito" % "mockito-core" % "5.10.0" % Test,
    ),
    (Test / testOptions) += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
