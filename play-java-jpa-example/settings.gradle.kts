rootProject.name = "play-java-jpa-example"

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
