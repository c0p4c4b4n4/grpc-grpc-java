package com.example.grpc.cancellation

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Loggers
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.take
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

      logger.info("Starting stream...")

      // .take(3) will automatically cancel the flow (and the RPC)
      // after the 3rd element is received.
      stub.serverStreamingEcho(request)
        .take(3)
        .collect { response ->
          logger.info("Response received: ${response.message}")
        }

    } catch (e: StatusRuntimeException) {
      logger.warning("RPC failed with status: ${e.status}")
    } catch (e: CancellationException) {
      logger.info("Client cancelled flow")
    } finally {
      channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
  }
}
