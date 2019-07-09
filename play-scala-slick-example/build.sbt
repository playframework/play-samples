lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-slick-example""",
    version := "2.8.x",
    scalaVersion := "2.13.0",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-slick" % "5.0.0-M4",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0-M4",
      "com.h2database" % "h2" % "1.4.199",
      specs2 % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )



