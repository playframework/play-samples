rootProject.name = "play-java-hello-world-tutorial"

// TODO: Remove after release Play Gradle Plugin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
        mavenLocal()
    }
}
