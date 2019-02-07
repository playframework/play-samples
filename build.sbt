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
