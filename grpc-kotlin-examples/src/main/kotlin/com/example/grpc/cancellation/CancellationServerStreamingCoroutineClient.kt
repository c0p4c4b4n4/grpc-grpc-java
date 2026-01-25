package com.example.grpc.cancellation

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Loggers
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import io.grpc.Status
import java.util.logging.Logger

object CancellationServerStreamingCoroutineClient {
  private val logger = Logger.getLogger(CancellationServerStreamingCoroutineClient::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) = runBlocking {
    Loggers.init()

    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
    try {
      val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)

      val request = echoRequest { message = "world" }
      stub.serverStreamingEcho(request)
        .take(3)
        .collect { response ->
          logger.info("response: ${response.message}")
        }
    } catch (e: StatusRuntimeException) {
      if (e.status.code == Status.Code.DEADLINE_EXCEEDED) {
        logger.warning("RPC error: deadline exceeded")
      } else {
        logger.warning("RPC error: ${e.status}")
      }
    } finally {
      channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
  }
}
