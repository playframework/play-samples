resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeIvyRepo("snapshots")
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-SNAPSHOT")
