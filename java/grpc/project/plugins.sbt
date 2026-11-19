enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.13.0-M5+111-8170b920-SNAPSHOT"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.java.grpc.sample"

resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-5da0b97a-SNAPSHOT")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.1")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "2.0.0")
libraryDependencies += "org.playframework" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
