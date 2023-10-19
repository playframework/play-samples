enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.9.1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"


addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0-M3")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.5")

// #grpc_sbt_plugin
// project/plugins.sbt
addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "1.0.2")
libraryDependencies += "com.lightbend.play" %% "play-grpc-generators" % playGrpcV
// #grpc_sbt_plugin
