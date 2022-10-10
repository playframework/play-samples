import play.core.PlayVersion.akkaVersion
import play.core.PlayVersion.akkaHttpVersion
import play.grpc.gen.scaladsl.{ PlayScalaClientCodeGenerator, PlayScalaServerCodeGenerator }
import com.typesafe.sbt.packager.docker.{ Cmd, CmdLike, DockerAlias, ExecCmd }
import play.scala.grpc.sample.BuildInfo

name := "play-scala-grpc-example"
version := "1.0-SNAPSHOT"


// #grpc_play_plugins
// build.sbt
lazy val `play-scala-grpc-example` = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
// #grpc_play_plugins
    .settings(
      akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala),
      // #grpc_client_generators
      // build.sbt
      akkaGrpcExtraGenerators += PlayScalaClientCodeGenerator,
      // #grpc_client_generators
      // #grpc_server_generators
      // build.sbt
      akkaGrpcExtraGenerators += PlayScalaServerCodeGenerator,
      // #grpc_server_generators
      PlayKeys.devSettings ++= Seq(
        "play.server.http.port" -> "disabled",
        "play.server.https.port" -> "9443",
        // Configures the keystore to use in Dev mode. This setting is equivalent to `play.server.https.keyStore.path`
        // in `application.conf`.
        "play.server.https.keyStore.path" -> "conf/selfsigned.keystore",
      )
    )
    .settings(
      // workaround to https://github.com/akka/akka-grpc/pull/470#issuecomment-442133680
      dockerBaseImage := "openjdk:8-alpine",
      dockerCommands  := 
        Seq.empty[CmdLike] ++
        Seq(
          Cmd("FROM", "openjdk:8-alpine"), 
          ExecCmd("RUN", "apk", "add", "--no-cache", "bash")
        ) ++
        dockerCommands.value.tail ,
      (Docker / dockerAliases) += DockerAlias(None, None, "play-scala-grpc-example", None),
      (Docker / packageName) := "play-scala-grpc-example",
    )
    .settings(
      libraryDependencies ++= CompileDeps ++ TestDeps
    )

val CompileDeps = Seq(
  guice,
  "com.lightbend.play"      %% "play-grpc-runtime"    % BuildInfo.playGrpcVersion,
  "com.typesafe.akka"       %% "akka-discovery"       % akkaVersion,
  "com.typesafe.akka"       %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka"       %% "akka-http-spray-json" % akkaHttpVersion,
  // Test Database
  "com.h2database" % "h2" % "1.4.199"
)

val playVersion = play.core.PlayVersion.current
val TestDeps = Seq(
  "com.lightbend.play"      %% "play-grpc-scalatest" % BuildInfo.playGrpcVersion % Test, 
  "com.lightbend.play"      %% "play-grpc-specs2"    % BuildInfo.playGrpcVersion % Test, 
  "com.typesafe.play"       %% "play-test"           % playVersion     % Test, 
  "com.typesafe.play"       %% "play-specs2"         % playVersion     % Test, 
  "org.scalatestplus.play"  %% "scalatestplus-play"  % "5.0.0" % Test, 
)

scalaVersion := "2.12.17"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

// Make verbose tests
(Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin) 
