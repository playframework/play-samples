resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeIvyRepo("snapshots")
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-2014-03-12-237b90f-SNAPSHOT")
