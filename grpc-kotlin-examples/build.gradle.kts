import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.google.protobuf") version System.getProperty("protobufPluginVersion")
    id("org.jetbrains.kotlin.jvm") version System.getProperty("kotlinPluginVersion")
    id("com.gradleup.shadow") version System.getProperty("shadowPluginVersion")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.grpc:grpc-protobuf:${project.findProperty("grpcVersion")}")
    implementation("io.grpc:grpc-stub:${project.findProperty("grpcVersion")}")

    implementation("io.grpc:grpc-kotlin-stub:${project.findProperty("grpcKotlinVersion")}")
    implementation("com.google.protobuf:protobuf-kotlin:${project.findProperty("protobufKotlinVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.findProperty("kotlinCoroutinesVersion")}")

    runtimeOnly("io.grpc:grpc-netty-shaded:${project.findProperty("grpcVersion")}")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

protobuf {
    protoc {
        artifact = libs.protoc.asProvider().get().toString()
    }
    plugins {
        create("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        create("grpckt") {
            artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
