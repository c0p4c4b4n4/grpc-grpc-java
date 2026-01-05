package com.example.grpc.echo.unary

import io.grpc.Server
import io.grpc.ServerBuilder
import com.example.grpc.EchoServiceGrpc

class HelloWorldServer(private val port: Int) {
  val server: Server = ServerBuilder.forPort(port).addService(HelloWorldService()).build()

  fun start() {
    server.start()
    println("Server started, listening on $port")
    Runtime.getRuntime()
      .addShutdownHook(
        Thread {
          println("*** shutting down gRPC server since JVM is shutting down")
          this@HelloWorldServer.stop()
          println("*** server shut down")
        },
      )
  }

  private fun stop() {
    server.shutdown()
  }

  fun blockUntilShutdown() {
    server.awaitTermination()
  }

  internal class HelloWorldService : EchoServiceGrpc.GreeterCoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest) = helloReply {
      message = "Hello ${request.name}"
    }
  }
}

fun main() {
  val port = System.getenv("PORT")?.toInt() ?: 50051
  val server = HelloWorldServer(port)
  server.start()
  server.blockUntilShutdown()
}
