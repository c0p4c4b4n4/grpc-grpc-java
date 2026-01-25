plugins {
  application

  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.protobuf)
}

dependencies {
  api(libs.kotlinx.coroutines.core)
  //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.findProperty("grpcVersion")}")

  api(libs.grpc.stub)
  //implementation("io.grpc:grpc-stub:${project.findProperty("grpcVersion")}")
  api(libs.grpc.protobuf)
  //implementation("io.grpc:grpc-protobuf:${project.findProperty("grpcVersion")}")
  api(libs.protobuf.java.util)
  //implementation("com.google.protobuf:protobuf-java-util:${project.findProperty("grpcVersion")}")
  api(libs.protobuf.kotlin)
  //implementation("com.google.protobuf:protobuf-kotlin:${project.findProperty("grpcVersion")}")
  api(libs.grpc.kotlin.stub)
  //implementation("io.grpc:grpc-kotlin-stub:${project.findProperty("grpcVersion")}")

//    runtimeOnly(libs.grpc.netty)
  runtimeOnly("io.grpc:grpc-netty-shaded:${project.findProperty("grpcVersion")}")
}

kotlin {
  jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions { freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn") }
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
