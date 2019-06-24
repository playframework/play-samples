import akka.grpc.gen.javadsl.play._
import com.typesafe.sbt.packager.docker.{ Cmd, CmdLike, DockerAlias, ExecCmd }

name := "play-java-grpc-example"
version := "1.0-SNAPSHOT"

// #grpc_play_plugins
// build.sbt
lazy val `play-java-grpc-example` = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
  // #grpc_play_plugins
  .settings(
  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
  // #grpc_client_generators
  // build.sbt
  akkaGrpcExtraGenerators += PlayJavaClientCodeGenerator,
  // #grpc_client_generators
  // #grpc_server_generators
  // build.sbt
  akkaGrpcExtraGenerators += PlayJavaServerCodeGenerator,
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
    dockerAliases in Docker += DockerAlias(None, None, "play-java-grpc-example", None),
    packageName in Docker := "play-java-grpc-example",
  )

scalaVersion := "2.12.8"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= Seq(
  guice,
  javaWs,
  // used in tests
  "com.lightbend.play" %% "play-grpc-testkit" % "0.7.0" % Test

)

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))



// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
