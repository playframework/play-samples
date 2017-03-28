// The Typesafe snapshots repository
// need to pull from
// https://oss.sonatype.org/content/repositories/snapshots/com/typesafe/play/play_2.11/maven-metadata.xml
// https://www.playframework.com/documentation/2.5.x/Repositories is incorrect!
resolvers += "Typesafe Ivy Snapshots Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-M3")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

