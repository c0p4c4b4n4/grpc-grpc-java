package com.example.grpc.methodtypes.streaming.client

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoResponse
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object ClientStreamingServer {
  private val logger = Logger.getLogger(ClientStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    val server = ServerBuilder
      .forPort(50051)
      .addService(EchoServiceImpl())
      .build()
      .start()

    logger.info("server started, listening on ${server.port}")

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
        emit(echoResponse { message = "hello ${request.message}" })
        emit(echoResponse { message = "guten tag ${request.message}" })
        emit(echoResponse { message = "bonjour ${request.message}" })
      }
    }
  }
}