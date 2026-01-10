package com.example.grpc.methodtypes.streaming.server

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object ServerStreamingServer {
  private val logger = Logger.getLogger(ServerStreamingServer::class.java.name)

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
    override fun serverStreamingEcho(request: EchoRequest): Flow<EchoResponse> {
      logger.info("request: ${request.message}")
      return flow {
        emit(EchoResponse.newBuilder().setMessage("hello ${request.message}").build())
        emit(EchoResponse.newBuilder().setMessage("guten tag ${request.message}").build())
        emit(EchoResponse.newBuilder().setMessage("bonjour ${request.message}").build())
      }
    }
  }
}