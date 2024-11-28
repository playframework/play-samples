enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.11.1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.java.grpc.sample"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.6")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.7")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "2.1.5")
libraryDependencies += "com.typesafe.play" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
