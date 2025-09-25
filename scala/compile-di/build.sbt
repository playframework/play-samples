resolvers += Resolver.sonatypeCentralSnapshots

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.6"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2",
    scalacOptions ++= List(
      //"-encoding", "utf8", // These three get set by Play automatically
      //"-deprecation",
      //"-unchecked",
      "-feature",
      "-Werror"
    ),
  )
