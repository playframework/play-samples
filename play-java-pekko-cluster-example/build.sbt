import play.core.PlayVersion

name := """play-java-pekko-cluster-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

crossScalaVersions := Seq("2.13.12", "3.3.1")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += guice

val pekkoVersion =  PlayVersion.pekkoVersion

// this dependency is required to form the Pekko Cluster
libraryDependencies += "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion

// Sending messages from a node to another in the Pekko Cluster requires serializing. This
// example application uses the default Pekko Jackson serializer with the CBOR format.
// See also `conf/serialization.conf` and `services.CborSerializable` for more info.
