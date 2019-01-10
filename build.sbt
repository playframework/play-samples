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

scalaVersion := "2.12.8"
crossScalaVersions := Seq("2.11.12", "2.12.8")
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

libraryDependencies += guice

// There is a bug in akka-http 10.1.4 that makes it not work with gRPC+Play,
// so we need to downgrade to 10.1.3 (or move to 10.1.5 when that's out)
// https://github.com/akka/akka-http/issues/2168
dependencyOverrides += "com.typesafe.akka" %% "akka-http-core" % "10.1.3"
dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.3"

// Test libraries
val playVersion = play.core.PlayVersion.current
val playGrpcVersion = "0.5.0-M7"
libraryDependencies += "com.lightbend.play"      %% "play-grpc-scalatest" % playGrpcVersion % Test
libraryDependencies += "com.lightbend.play"      %% "play-grpc-specs2"    % playGrpcVersion % Test
libraryDependencies += "com.typesafe.play"       %% "play-test"           % playVersion     % Test
libraryDependencies += "com.typesafe.play"       %% "play-specs2"         % playVersion     % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0-RC2" % Test

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj"    % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility"   % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
