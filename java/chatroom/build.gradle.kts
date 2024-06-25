import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaVersion:${libs.versions.play.get()}"))

    implementation("org.playframework:play-java-forms_$scalaVersion")
    implementation("org.playframework:play-pekko-http-server_$scalaVersion")
    implementation("org.playframework:play-ahc-ws_$scalaVersion")
    implementation("org.playframework:play-guice_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")
    implementation("org.webjars:webjars-play_$scalaVersion:${libs.versions.webjars.play.get()}")
    implementation(libs.flot)
    implementation(libs.bootstrap)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
    testImplementation(libs.assertj)
    testImplementation(libs.awaitility)
}

tasks.test {
    systemProperty("testserver.port", "19001")
}

tasks.withType<ScalaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"))
}

// TODO: Remove after release Play Gradle Plugin
repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    mavenLocal()
}
