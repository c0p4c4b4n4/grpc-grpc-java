package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoResponse
import io.grpc.Server
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object UnaryServer {
  private val logger = Logger.getLogger(UnaryServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    val port = 50051
    val server: Server = ServerBuilder
      .forPort(port)
      .addService(EchoServiceImpl())
      .build()
      .start()

    logger.info("server started, listening on $port")

    Runtime.getRuntime().addShutdownHook(
      Thread {
        System.err.println("server is shutting down")
        try {
          server.shutdown().awaitTermination(10, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
          server.shutdownNow()
        }
        System.err.println("server has been shut down")
      }
    )

    server.awaitTermination()
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) = echoResponse {
      message = "hello ${request.message}"
    }
  }
}