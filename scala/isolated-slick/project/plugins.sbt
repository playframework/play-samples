libraryDependencies += "com.h2database" % "h2" % "2.5.250"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "12.0.0")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.2.0")

// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-e1f3c2a9-SNAPSHOT")
