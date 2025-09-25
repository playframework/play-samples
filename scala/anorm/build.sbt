resolvers += Resolver.sonatypeCentralSnapshots

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-anorm-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.6"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      evolutions,
      "com.h2database" % "h2" % "2.3.232",
      "org.playframework.anorm" %% "anorm" % "2.9.0-M1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test,
    ),
    scalacOptions ++= List("-feature", "-Werror"),
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
  )
