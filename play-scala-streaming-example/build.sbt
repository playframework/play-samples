lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-streaming-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16", "3.3.6"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      ws % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.2" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  )

TwirlKeys.templateImports ++= Seq(
  "views.html.helper.CSPNonce"
)
