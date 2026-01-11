package com.example.grpc.methodtypes.streaming.bidirectional

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object BidirectionalStreamingServer {
  private val logger = Logger.getLogger(BidirectionalStreamingServer::class.java.name)

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
    override fun bidirectionalStreamingEcho(requests: Flow<EchoRequest>): Flow<EchoResponse> = flow {
      requests.collect { request ->
        logger.info("next request: ${request.message}")
        emit(EchoResponse.newBuilder().setMessage("hello ${request.message}").build())
        emit(EchoResponse.newBuilder().setMessage("guten tag ${request.message}").build())
        emit(EchoResponse.newBuilder().setMessage("bonjour ${request.message}").build())
      }
    }
  }
}