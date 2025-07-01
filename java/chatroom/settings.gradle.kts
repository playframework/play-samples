rootProject.name = "play-java-chatroom-example"

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
