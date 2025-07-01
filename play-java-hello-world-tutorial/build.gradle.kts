import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaVersion:${libs.versions.play.get()}"))

    implementation("org.playframework:play-pekko-http-server_$scalaVersion")
    implementation("org.playframework:play-guice_$scalaVersion")
    implementation("org.playframework:play-java-forms_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
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
    mavenLocal()
}
