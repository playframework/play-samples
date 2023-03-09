import play.core.PlayVersion.akkaVersion
import play.core.PlayVersion.akkaHttpVersion
import play.grpc.gen.javadsl.{ PlayJavaClientCodeGenerator, PlayJavaServerCodeGenerator }
import com.typesafe.sbt.packager.docker.{ Cmd, CmdLike, DockerAlias, ExecCmd }
import play.java.grpc.sample.BuildInfo

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
    Test / javaOptions += "-Dtestserver.httpsport=9443",
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
    dockerBaseImage := "eclipse-temurin:11-alpine",
    dockerCommands  :=
      Seq.empty[CmdLike] ++
        Seq(
          Cmd("FROM", "eclipse-temurin:11-alpine"),
          ExecCmd("RUN", "apk", "add", "--no-cache", "bash")
        ) ++
        dockerCommands.value.tail ,
    (Docker / dockerAliases) += DockerAlias(None, None, "play-java-grpc-example", None),
    (Docker / packageName) := "play-java-grpc-example",
  )
  .settings(
    libraryDependencies ++= CompileDeps ++ TestDeps
  )
  
scalaVersion := "2.13.12"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation")
// Needed for ssl-config to create self signed certificated under Java 17
Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED")

val CompileDeps = Seq(
  guice,
  javaWs,
  "com.typesafe.play"       %% "play-grpc-runtime"    % BuildInfo.playGrpcVersion,
  "com.typesafe.akka"       %% "akka-discovery"       % akkaVersion,
  "com.typesafe.akka"       %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka"       %% "akka-http-spray-json" % akkaHttpVersion,
  // Test Database
  "com.h2database" % "h2" % "2.2.224"
)

val TestDeps = Seq(
  // used in tests
  "com.typesafe.play" %% "play-grpc-testkit" % BuildInfo.playGrpcVersion % Test
)

// Make verbose tests
(Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
