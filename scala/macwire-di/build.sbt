resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-macwire-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.4"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % "2.6.5" % "provided",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M1" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
