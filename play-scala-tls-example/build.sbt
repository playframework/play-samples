lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val core = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test,
)

lazy val scala3Deps = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1631-SNAPSHOT" % Test,
)

val commonSettings = Seq(
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala213, scala3),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          "-feature",
          "-deprecation",
          "-Xfatal-warnings",
          "-Xsource:3",
        )
      case _ => Nil
    }
  }
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
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => scala3Deps
        case _ => core
      }
    }
  )
  .aggregate(one, two)
  .dependsOn(one, two)

addCommandAlias("client", "runMain Main")
