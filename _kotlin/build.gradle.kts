plugins {
    kotlin("jvm") version "2.2.0"
    id("com.google.protobuf") version protobufPluginVersion
    id("com.gradleup.shadow") version shadowPluginVersion
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // --- gRPC core ---
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")

    // --- gRPC Kotlin ---
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    // --- Protobuf Kotlin ---
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    // --- Transport ---
    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")

    // --- Kotlin ---
    implementation(kotlin("stdlib"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        // Java gRPC codegen (optional but often useful)
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }

        // Kotlin gRPC codegen
        id("grpckt") {
            artifact =
                "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
