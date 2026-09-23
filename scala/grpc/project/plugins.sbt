enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.13.0-M5+70-4d1a20b1-SNAPSHOT"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"

resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-e1f3c2a9-SNAPSHOT")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.0")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.2.0")
libraryDependencies += "org.playframework" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
