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
    implementation("org.playframework:play-java-jpa_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")
    implementation(libs.failsafe)
    implementation(libs.url.builder)
    implementation(libs.h2)
    implementation(libs.hibernate.core)
    implementation(libs.metrics.core)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
    testImplementation("org.scalatestplus.play", "scalatestplus-play_$scalaVersion", libs.versions.scalatestplus.play.get())
}

// TODO: Remove after release Play Gradle Plugin
repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    mavenLocal()
}
