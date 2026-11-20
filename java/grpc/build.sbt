import play.core.PlayVersion.pekkoVersion
import play.core.PlayVersion.pekkoHttpVersion
import play.grpc.gen.javadsl.{PlayJavaClientCodeGenerator, PlayJavaServerCodeGenerator}
import play.java.grpc.sample.BuildInfo

resolvers += Resolver.sonatypeCentralSnapshots

name := "play-java-grpc-example"
version := "1.0-SNAPSHOT"

// #grpc_play_plugins
// build.sbt
lazy val `play-java-grpc-example` = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(PekkoGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayPekkoHttp2Support) // enables serving HTTP/2 and gRPC
  // #grpc_play_plugins
  .settings(
    pekkoGrpcGeneratedLanguages := Seq(PekkoGrpc.Java),
    // #grpc_client_generators
    // build.sbt
    pekkoGrpcExtraGenerators += PlayJavaClientCodeGenerator,
    // #grpc_client_generators
    // #grpc_server_generators
    // build.sbt
    pekkoGrpcExtraGenerators += PlayJavaServerCodeGenerator,
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
    dockerBaseImage := "eclipse-temurin:25-jre-resolute",
    dockerExposedPorts := Seq(9000, 9443),
    // gRPC's Netty transport loads native libraries.
    Universal / javaOptions += "-J--enable-native-access=ALL-UNNAMED",
    (Docker / packageName) := "play-java-grpc-example",
  )
  .settings(
    libraryDependencies ++= CompileDeps ++ TestDeps
  )

scalaVersion := "2.13.18"
crossScalaVersions := Seq("2.13.18", "3.3.9")
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation")

val CompileDeps = Seq(
  guice,
  javaWs,
  "org.playframework"      %% "play-grpc-runtime"     % BuildInfo.playGrpcVersion,
  "org.apache.pekko"       %% "pekko-discovery"       % pekkoVersion,
  "org.apache.pekko"       %% "pekko-http"            % pekkoHttpVersion,
  "org.apache.pekko"       %% "pekko-http-spray-json" % pekkoHttpVersion,
  // Test Database
  "com.h2database" % "h2" % "2.5.252"
)

val TestDeps = Seq(
  // used in tests
  "org.playframework" %% "play-grpc-testkit" % BuildInfo.playGrpcVersion % Test
)

// Make verbose tests
(Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
  .settings(name := "play-java-grpc-example-docs")
