package play.scala.grpc.sample

import sbt._
import sbt.Keys._

object Dependencies {

  // Test libraries
  val playVersion = play.core.PlayVersion.current

  val playGrpcVersion = "0.8.1"
  val CompileDeps = Seq(
    play.sbt.PlayImport.guice,
    "com.lightbend.play"      %% "play-grpc-runtime"   % BuildInfo.playGrpcVersion, 
    "com.typesafe.akka"       %% "akka-discovery"      % "2.6.1", 
    "com.typesafe.akka"       %% "akka-http"           % "10.1.11",
    // Test Database
    "com.h2database" % "h2" % "1.4.199"
  )

  val TestDeps = Seq(
    "com.lightbend.play"      %% "play-grpc-scalatest" % BuildInfo.playGrpcVersion % Test, 
    "com.lightbend.play"      %% "play-grpc-specs2"    % BuildInfo.playGrpcVersion % Test, 
    "com.typesafe.play"       %% "play-test"           % playVersion     % Test, 
    "com.typesafe.play"       %% "play-specs2"         % playVersion     % Test, 
    "org.scalatestplus.play"  %% "scalatestplus-play"  % "5.0.0" % Test, 
  )
}