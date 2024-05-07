import play.core.PlayVersion

name := """play-java-akka-cluster-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend

crossScalaVersions := Seq("2.13.14", "3.3.3")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += guice

val akkaVersion =  PlayVersion.akkaVersion

// this dependency is required to form the Akka Cluster
libraryDependencies += ("com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion).cross(CrossVersion.for3Use2_13)

// Sending messages from a node to another in the Akka Cluster requires serializing. This
// example application uses the default Akka Jackson serializer with the CBOR format.
// See also `conf/serialization.conf` and `services.CborSerializable` for more info.
