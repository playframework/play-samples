import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
}

val scalaBinaryVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).let {
    if (it.startsWith("3")) "3" else it.split('.').take(2).joinToString(".")
}
val server = System.getProperty("backend.server", "netty").let { if (it == "pekko") "pekko-http" else it }

scala {
    scalaVersion = providers.systemProperty("scala.version")
}

twirl {
    scalaVersion = scalaBinaryVersion
}

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaBinaryVersion:${libs.versions.play.get()}"))

    implementation("org.playframework:play-$server-server_$scalaBinaryVersion")
    implementation("org.playframework:play-guice_$scalaBinaryVersion")
    implementation("org.playframework:play-logback_$scalaBinaryVersion")
    implementation("org.playframework:play-java-forms_$scalaBinaryVersion")
    implementation("org.apache.pekko:pekko-cluster-typed_$scalaBinaryVersion:${libs.versions.pekko.get()}")

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaBinaryVersion")
}

tasks.test {
    jvmArgs("--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED")
}

tasks.withType<ScalaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"))
}

// TODO: Remove after release Play Gradle Plugin
repositories {
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
    }
    maven {
        url = uri("https://repository.apache.org/content/repositories/snapshots/")
    }
    mavenLocal()
}

// Play 3.1 requires Java 17, including the Scala compiler's Java output target.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ScalaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--release", "17"))
    scalaCompileOptions.additionalParameters = (scalaCompileOptions.additionalParameters ?: emptyList()) + "-release:17"
}
