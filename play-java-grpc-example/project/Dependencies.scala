package play.java.grpc.sample

import sbt._
import sbt.Keys._

object Dependencies {

  // Test libraries
  val playVersion = play.core.PlayVersion.current

  val playGrpcVersion = "0.8.1"
  val CompileDeps = Seq(
    play.sbt.PlayImport.guice,
    play.sbt.PlayImport.javaWs,
    "com.lightbend.play"      %% "play-grpc-runtime"   % BuildInfo.playGrpcVersion, 
    "com.typesafe.akka"       %% "akka-discovery"      % "2.6.1", 
    "com.typesafe.akka"       %% "akka-http"           % "10.1.11",
    // Test Database
    "com.h2database" % "h2" % "1.4.199"
  )

  val TestDeps = Seq(
    // used in tests
    "com.lightbend.play" %% "play-grpc-testkit" % playGrpcVersion % Test,
    // Testing libraries for dealing with CompletionStage...
    // "org.assertj"    % "assertj-core" % "3.6.2" % Test,
    // "org.awaitility" % "awaitility"   % "2.0.0" % Test
  )
}