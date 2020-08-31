import play.core.PlayVersion

name := """play-java-akka-cluster-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice

// In order to use Akka Cluster SBR-OSS for the cluster we need to override the Akka version
// and use, at least, Akka 2.6.6
//val akkaVersion =  PlayVersion.akkaVersion
val akkaVersion =  "2.6.6"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion

// Sending messages from a node to another in the Akka Cluster requires serializing the message. This
// example application uses the default Akka Jackson serializer with the CBOR serialization format.
// See also `conf/serialization.conf` and `services.CborSerializable` for more info.
libraryDependencies += "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion


// Some Akka overrides to align versions of artifacts
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",
  "com.typesafe.akka" %% "akka-actor" % "2.6.5",
)