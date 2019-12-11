name := """play-tls-example"""

version := "1.0.0"

lazy val one = (project in file("modules/one")).enablePlugins(PlayScala)

lazy val two = (project in file("modules/two")).enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayAkkaHttp2Support)
  .aggregate(one, two)
  .dependsOn(one, two)

scalaVersion := "2.13.0"

libraryDependencies += ws
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// workaround for https://github.com/playframework/playframework/issues/9896
libraryDependencies += "com.typesafe" %% "ssl-config-core" % "0.4.0"

fork in run := true

// Uncomment if you want to run "./play client" explicitly without SNI.
//javaOptions in run += "-Djsse.enableSNIExtension=false"

javaOptions in run += "-Djavax.net.debug=ssl:handshake"

addCommandAlias("client", "runMain Main")

// Must not run tests in fork because the `play` script sets
// some JVM properties (-D) which tests need.
fork in Test := false