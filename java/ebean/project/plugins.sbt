// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-e1f3c2a9-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "9.0.0-M2+80-59a82594-SNAPSHOT")
