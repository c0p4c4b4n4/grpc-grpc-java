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
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:${grpcVersion}")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    implementation(kotlin("stdlib"))

    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions { freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn") }
}

protobuf {
    protoc { artifact = libs.protoc.asProvider().get().toString() }
    plugins {
        create("grpc") { artifact = libs.protoc.gen.grpc.java.get().toString() }
        create("grpckt") { artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins { create("kotlin") }
        }
    }
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:$protobufVersion"
//    }
//
//    plugins {
////        id("grpc") {
////            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
////        }
//        id("grpckt") {
//            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
//        }
//    }
//
//    generateProtoTasks {
//        all().forEach {
////            task ->
////            task.plugins {
//////                id("grpc")
////                id("grpckt")
////            }
//            it.plugins {
//                // Create a plugin configuration for gRPC Kotlin
//                create("grpckt") {
//                    artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.0:jdk8@jar" // Use a compatible gRPC Kotlin version
//                }
//            }
//            it.builtins {
//                // Also create a configuration for the default Kotlin built-in generator
//                create("kotlin")
//            }
//        }
//    }
//}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
