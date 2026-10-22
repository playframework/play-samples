libraryDependencies += "com.h2database" % "h2" % "2.5.252"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "12.0.0")

// Slick code generation
// https://github.com/sbt/sbt-slick-codegen
addSbtPlugin("com.github.sbt" % "sbt-slick-codegen" % "2.2.0+26-1d7c932e-SNAPSHOT")

// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-3968a052-SNAPSHOT")
