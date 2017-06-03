// The Play plugin

resolvers += Resolver.sonatypeRepo("snapshots") 

//updateOptions := updateOptions.value.withLatestSnapshots(false)

// https://oss.sonatype.org/content/repositories/snapshots/com/typesafe/play/play_2.11/maven-metadata.xml
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-RC2")

