// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-5da0b97a-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "9.0.0-M2+174-f3e0b8d6-SNAPSHOT")
