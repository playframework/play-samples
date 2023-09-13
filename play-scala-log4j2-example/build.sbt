val log4jVersion = "2.20.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback)
  .settings(
    name := """play-scala-log4j2-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
