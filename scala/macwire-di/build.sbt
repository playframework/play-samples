resolvers += Resolver.sonatypeCentralSnapshots

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-macwire-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.17", "3.3.7"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % "2.6.6" % "provided",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ),
  )
