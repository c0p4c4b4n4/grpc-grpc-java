package com.example.grpc.deadline

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Loggers
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object DeadlineServerStreamingCoroutineClient {
  private val logger = Logger.getLogger(DeadlineServerStreamingCoroutineClient::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) = runBlocking {
    Loggers.init()

    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
    try {
      val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)
        .withDeadlineAfter(3, TimeUnit.SECONDS)

      val request = echoRequest { this.message = "world" }
      stub.serverStreamingEcho(request)
        .onCompletion { cause ->
          if (cause != null) {
            logger.warning("stream failed: ${cause.message}")
          } else {
            logger.info("stream succeed")
          }
        }
        .collect { response ->
          logger.info("response: ${response.message}")
        }
    } catch (e: StatusRuntimeException) {
      logger.warning("RPC error: ${e.status}")
    } finally {
      channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
  }
}
