import akka.grpc.gen.scaladsl.play._

name := """akka-grpc-play-quickstart-scala"""
version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
    .settings(
      akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala),
      akkaGrpcExtraGenerators += PlayScalaClientCodeGenerator,
      akkaGrpcExtraGenerators += PlayScalaServerCodeGenerator,
      PlayKeys.devSettings ++= Seq(
        "play.server.http.port" -> "disabled",
        "play.server.https.port" -> "9443",
        "play.server.https.keyStore.path" -> "conf/selfsigned.keystore",
      )
    )

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.6")

libraryDependencies += guice

// There is a bug in akka-http 10.1.4 that makes it not work with gRPC+Play,
// so we need to downgrade to 10.1.3 (or move to 10.1.5 when that's out)
// https://github.com/akka/akka-http/issues/2168
dependencyOverrides += "com.typesafe.akka" %% "akka-http-core" % "10.1.3"
dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.3"

// Test libraries
resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies += "com.lightbend.akka.grpc" %% "akka-grpc-play-testkit" % "0.4.1"     % Test
libraryDependencies += "com.typesafe.play"       %% "play-test"              % "2.7.0-M3"  % Test
libraryDependencies += "com.typesafe.play"       %% "play-specs2"            % "2.7.0-M3"  % Test
libraryDependencies += "org.scalatestplus.play"  %% "scalatestplus-play"     % "4.0.0-M1"  % Test

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))


