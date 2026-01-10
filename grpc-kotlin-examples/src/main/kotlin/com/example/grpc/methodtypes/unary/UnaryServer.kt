package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoResponse
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.examples.helloworld.helloReply

class UnaryServer(private val port: Int) {
  val server: Server = ServerBuilder.forPort(port).addService(EchoServiceImpl()).build()

  fun start() {
    server.start()
    println("Server started, listening on $port")
    Runtime.getRuntime()
      .addShutdownHook(
        Thread {
          println("*** shutting down gRPC server since JVM is shutting down")
          this@UnaryServer.stop()
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

  internal class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) = echoResponse {
        message = "Hello ${request.message}"
    }
  }
}

fun main() {
  val port = 50051
  val server = UnaryServer(port)
  server.start()
  server.blockUntilShutdown()
}
