import akka.grpc.gen.javadsl.play._

name := """akka-grpc-play-quickstart-java"""
version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
    .settings(
      akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
      akkaGrpcExtraGenerators += PlayJavaClientCodeGenerator,
      akkaGrpcExtraGenerators += PlayJavaServerCodeGenerator,
      PlayKeys.devSettings ++= Seq(
        "play.server.http.port" -> "disabled",
        "play.server.https.port" -> "9443",
        "play.server.https.keyStore.path" -> "conf/selfsigned.keystore",
      )
    )

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.6")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
