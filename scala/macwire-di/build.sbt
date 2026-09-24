resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)

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
    name := """play-scala-macwire-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.3.8"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % "2.6.7" % "provided",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2+52-be104c90-SNAPSHOT" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ) ++ scala2OnlyScalacOptions("-Xsource:3").value,
  )
