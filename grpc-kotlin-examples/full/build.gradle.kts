plugins {
  application
  alias(libs.plugins.kotlin.jvm)
}

kotlin { jvmToolchain(17) }

dependencies {
  implementation(project(":stub"))
  runtimeOnly(libs.grpc.netty)
}

//tasks.register<JavaExec>("HelloWorldServer") {
//  dependsOn("classes")
//  classpath = sourceSets["main"].runtimeClasspath
//  mainClass.set("io.grpc.examples.helloworld.HelloWorldServerKt")
//}
//
//tasks.register<JavaExec>("RouteGuideServer") {
//  dependsOn("classes")
//  classpath = sourceSets["main"].runtimeClasspath
//  mainClass.set("io.grpc.examples.routeguide.RouteGuideServerKt")
//}
//
//tasks.register<JavaExec>("HelloWorldClient") {
//    dependsOn("classes")
//    classpath = sourceSets["main"].runtimeClasspath
//    mainClass.set("io.grpc.examples.helloworld.HelloWorldClientKt")
//}
//
//tasks.register<JavaExec>("RouteGuideClient") {
//    dependsOn("classes")
//    classpath = sourceSets["main"].runtimeClasspath
//    mainClass.set("io.grpc.examples.routeguide.RouteGuideClientKt")
//}
