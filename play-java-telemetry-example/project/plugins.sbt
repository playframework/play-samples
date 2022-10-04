lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0 (maybe even 1.7.2), see https://github.com/sbt/sbt/pull/7021
)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-M2")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.12.4")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
