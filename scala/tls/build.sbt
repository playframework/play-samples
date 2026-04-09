def scala2OnlyScalacOptions(options: String*) = Def.setting {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => options
    case _            => Seq.empty
  }
}

val commonSettings = Seq(
  resolvers += Resolver.sonatypeCentralSnapshots,
  crossScalaVersions := Seq("2.13.18", "3.8.3"),
  scalaVersion := crossScalaVersions.value.head,
  scalacOptions ++= Seq(
    "-feature",
    "-Werror"
  ) ++ scala2OnlyScalacOptions("-Xsource:3").value
)

lazy val one = (project in file("modules/one"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)

lazy val two = (project in file("modules/two"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayPekkoHttp2Support)
  .settings(commonSettings)
  .settings(
    name := """play-scala-tls-example""",
    version := "1.0.0",
    (run / fork) := true,
    
    // Uncomment if you want to run "./play client" explicitly without SNI.
    //(run / javaOptions) += "-Djsse.enableSNIExtension=false"
    (run / javaOptions) += "-Djavax.net.debug=ssl:handshake",

    // Must not run tests in fork because the `play` script sets
    // some JVM properties (-D) which tests need.
    (Test / fork) := false,

    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test,
    )
  )
  .aggregate(one, two)
  .dependsOn(one, two)

addCommandAlias("client", "runMain Main")
