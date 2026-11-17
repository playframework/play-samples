resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-3968a052-SNAPSHOT")

addSbtPlugin("com.github.sbt" % "sbt-less" % "2.1.0-M2+41-00c929d9-SNAPSHOT")

addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.1.0-M3+29-99381aea-SNAPSHOT")
