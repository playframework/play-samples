lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "3.3.0-RC3",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test,
    scalacOptions ++= List(
      "utf8",
      "-feature",
      "-Xfatal-warnings",
      "-source:3.0-migration",
      "-rewrite",
      "-explain"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
