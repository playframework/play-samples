lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-anorm-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.11",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      evolutions,
      "com.h2database" % "h2" % "1.4.200",
      "org.playframework.anorm" %% "anorm" % "2.7.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    ),
    scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
