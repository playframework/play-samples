enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.13.0-M1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"

resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M3")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.7")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.1.1")
libraryDependencies += "org.playframework" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
