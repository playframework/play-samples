// You will need private bintray credentials to publish this with Lightbend theme
// credentials += Credentials("Bintray", "dl.bintray.com", "<user>", "<bintray API key>")
// resolvers += "bintray-typesafe-internal-maven-releases" at "https://dl.bintray.com/typesafe/internal-maven-releases/"
// paradoxTheme := Some("com.lightbend.paradox" % "paradox-theme-lightbend" % "0.2.3")

// Uses the out of the box generic theme.
paradoxTheme := Some(builtinParadoxTheme("generic"))

