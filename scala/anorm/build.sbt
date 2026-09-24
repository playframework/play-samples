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
    name := "play-scala-anorm-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.3.8"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      evolutions,
      "com.h2database" % "h2" % "2.5.250",
      "org.playframework.anorm" %% "anorm" % "3.1.1-71d9aa79-SNAPSHOT",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2+52-be104c90-SNAPSHOT" % Test,
    ),
    scalacOptions ++= List("-feature", "-Werror") ++ scala2OnlyScalacOptions("-Xsource:3").value,
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
  )
