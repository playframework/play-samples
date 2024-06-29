// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-4009e5a0-SNAPSHOT")

// Load testing tool:
// https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.9.0")
