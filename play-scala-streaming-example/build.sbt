lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-streaming-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.12", "3.3.1"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      ws % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  )
