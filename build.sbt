import akka.grpc.gen.javadsl.play._

val `play-java-grpc-example` = (project in file("."))
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

scalaVersion := "2.12.8"
crossScalaVersions := Seq("2.11.12", "2.12.8")
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
javacOptions -= "-Werror" // https://github.com/akka/akka-grpc/issues/490

libraryDependencies += guice

// Test libraries
val playVersion = play.core.PlayVersion.current
val playGrpcVersion = "0.5.0"
libraryDependencies += "com.lightbend.play"      %% "play-grpc-scalatest" % playGrpcVersion % Test
libraryDependencies += "com.lightbend.play"      %% "play-grpc-specs2"    % playGrpcVersion % Test
libraryDependencies += "com.typesafe.play"       %% "play-test"           % playVersion     % Test
libraryDependencies += "com.typesafe.play"       %% "play-specs2"         % playVersion     % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))


// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
