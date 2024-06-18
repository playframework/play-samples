import play.core.PlayVersion.pekkoVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-websocket-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.14", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
      "org.awaitility" % "awaitility" % "4.2.1" % Test,
    ),
    TwirlKeys.templateImports ++= Seq(
      "views.html.helper.CSPNonce"
    ),
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  )
