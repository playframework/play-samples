resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Flyway" at "https://flywaydb.org/repo"

resolvers += "Flyway SBT" at "https://davidmweber.github.io/flyway-sbt.repo"

// Database migration
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "6.0.7")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.4.0")

libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.1")
