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
    name := "play-scala-anorm-example",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.8.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      evolutions,
      "com.h2database" % "h2" % "2.3.232",
      "org.playframework.anorm" %% "anorm" % "2.9.0-M1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test,
    ),
    scalacOptions ++= List("-feature", "-Werror") ++ scala2OnlyScalacOptions("-Xsource:3").value,
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
  )

