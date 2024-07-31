libraryDependencies += "com.h2database" % "h2" % "1.4.200" // Can't use latest h2 currently: https://github.com/flyway/flyway-sbt/issues/82#issuecomment-1636728997

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "7.4.0")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.2.0")

// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-d1f1b9f8-SNAPSHOT")
