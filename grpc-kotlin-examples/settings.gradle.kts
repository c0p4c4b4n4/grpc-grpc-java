rootProject.name = "grpc-kotlin-examples"

include(
    "protos",
    "stub",
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }
