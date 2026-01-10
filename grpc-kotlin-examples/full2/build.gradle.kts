plugins {
    application
    kotlin("jvm") version "1.9.23"
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // gRPC + Protobuf
    implementation("io.grpc:grpc-stub:1.63.0")
    implementation("io.grpc:grpc-protobuf:1.63.0")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")

    implementation("com.google.protobuf:protobuf-kotlin:3.25.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // Transport (runtime only)
    runtimeOnly("io.grpc:grpc-netty:1.63.0")
}

application {
    mainClass.set("com.example.main.AppKt")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.63.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
            task.builtins {
                id("kotlin")
            }
        }
    }
}
