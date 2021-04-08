// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.12.4")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
