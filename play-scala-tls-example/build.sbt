val commonSettings = Seq(
  crossScalaVersions := Seq("2.13.15", "3.3.3"),
  scalaVersion := crossScalaVersions.value.head,
  scalacOptions ++= Seq(
    "-feature",
    "-Werror"
  )
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
    //javaOptions in run += "-Djsse.enableSNIExtension=false"
    (run / javaOptions) += "-Djavax.net.debug=ssl:handshake",

    // Must not run tests in fork because the `play` script sets
    // some JVM properties (-D) which tests need.
    (Test / fork) := false,

    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    )
  )
  .aggregate(one, two)
  .dependsOn(one, two)

addCommandAlias("client", "runMain Main")
