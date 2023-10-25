// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.17.3")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
