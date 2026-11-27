resolvers += Resolver.sonatypeCentralSnapshots

def scala2OnlyScalacOptions(options: String*) = Def.setting {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => options
    case _            => Seq.empty
  }
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-websocket-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.3.9"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" %% "webjars-play" % "3.1.0-M5+7-9491b067-SNAPSHOT",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "5.3.8",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2+99-ae5a27dd-SNAPSHOT" % Test,
      "org.awaitility" % "awaitility" % "4.4.0" % Test,
    ),
    TwirlKeys.templateImports ++= Seq(
      "views.html.helper.CSPNonce"
    ),
    LessKeys.compress := true,
    (Test / javaOptions) += "-Dtestserver.port=19001",
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ) ++ scala2OnlyScalacOptions("-Xsource:3").value
  )
