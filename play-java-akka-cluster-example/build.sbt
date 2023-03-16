import play.core.PlayVersion

lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val core = Seq(
  guice,
)

lazy val akkaVersion =  "2.6.20"

lazy val akkaDeps = Seq(
// Some Akka overrides to align versions of artifacts
  ("com.typesafe.akka" %% "akka-stream" % akkaVersion).cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-actor" % akkaVersion).cross(CrossVersion.for3Use2_13),
  // this dependency is required to form the Akka Cluster
  ("com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion).cross(CrossVersion.for3Use2_13),
  // Sending messages from a node to another in the Akka Cluster requires serializing. This
  // example application uses the default Akka Jackson serializer with the CBOR format.
  // See also `conf/serialization.conf` and `services.CborSerializable` for more info.
  ("com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion).cross(CrossVersion.for3Use2_13),
)

name := """play-java-akka-cluster-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := scala213
crossScalaVersions := Seq(scala213, scala3)

libraryDependencies ++= core ++ akkaDeps

excludeDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) =>
      Seq(
        ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
      )
    case _ => Nil
  }
}
