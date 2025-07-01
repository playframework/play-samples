rootProject.name = "play-java-rest-api-example"

// TODO: Remove after release Play Gradle Plugin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
        }
        mavenLocal()
    }
}
