enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.10.0"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.java.grpc.sample"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.21")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "2.1.5")
libraryDependencies += "com.typesafe.play" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
