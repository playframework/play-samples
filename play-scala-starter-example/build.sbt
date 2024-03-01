lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-starter-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.13", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "2.2.224",
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.1" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
