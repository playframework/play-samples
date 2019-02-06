import akka.grpc.gen.javadsl.play._
import com.typesafe.sbt.packager.docker.{ Cmd, CmdLike, DockerAlias, ExecCmd }

name := "play-java-grpc-example"
version := "1.0-SNAPSHOT"

lazy val `play-scala-grpc-example` = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
  .settings(
  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
  akkaGrpcExtraGenerators += PlayJavaClientCodeGenerator,
  akkaGrpcExtraGenerators += PlayJavaServerCodeGenerator,
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
    dockerAliases in Docker += DockerAlias(None, None, "play-scala-grpc-example", None),
    packageName in Docker := "play-scala-grpc-example",
  )

scalaVersion := "2.12.8"
crossScalaVersions := Seq("2.11.12", "2.12.8")
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= Seq(
  guice,
  javaWs,
  "com.lightbend.play" %% "play-grpc-testkit" % "0.5.0" % Test
)

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))



// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
