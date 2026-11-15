// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-3968a052-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "9.0.0-M2+167-087fd01e-SNAPSHOT")
