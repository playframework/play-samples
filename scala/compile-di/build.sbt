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
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.18", "3.8.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2",
    scalacOptions ++= List(
      //"-encoding", "utf8", // These three get set by Play automatically
      //"-deprecation",
      //"-unchecked",
      "-feature",
      "-Werror"
    ) ++ scala2OnlyScalacOptions("-Xsource:3").value,
  )
