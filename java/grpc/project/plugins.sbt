enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.13.0-M5+86-69b1467c-SNAPSHOT"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.java.grpc.sample"

resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-3968a052-SNAPSHOT")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.1")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "2.0.0-M2")
libraryDependencies += "org.playframework" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
