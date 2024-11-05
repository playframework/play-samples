lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.15", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1",
    scalacOptions ++= List(
      //"-encoding", "utf8", // These three get set by Play automatically
      //"-deprecation",
      //"-unchecked",
      "-feature",
      "-Werror"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
