resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-e1f3c2a9-SNAPSHOT")

addSbtPlugin("com.github.sbt" % "sbt-less" % "2.1.0-M1")

addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.1.0-M2")
