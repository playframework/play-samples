val commonSettings = Seq(
  scalaVersion := "2.13.1",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
  )
)

lazy val one = (project in file("modules/one"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)

lazy val two = (project in file("modules/two"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayAkkaHttp2Support)
  .settings(commonSettings)
  .settings(
    name := """play-scala-tls-example""",
    version := "1.0.0",
    fork in run := true,
    
    // Uncomment if you want to run "./play client" explicitly without SNI.
    //javaOptions in run += "-Djsse.enableSNIExtension=false"
    javaOptions in run += "-Djavax.net.debug=ssl:handshake",

    // Must not run tests in fork because the `play` script sets
    // some JVM properties (-D) which tests need.
    fork in Test := false,

    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    )
  )
  .aggregate(one, two)
  .dependsOn(one, two)

addCommandAlias("client", "runMain Main")
