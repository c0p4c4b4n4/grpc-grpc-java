package com.example.grpc.methodtype.streaming.bidirectional

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Loggers
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object BidirectionalStreamingCoroutineClient {
  private val logger = Logger.getLogger(BidirectionalStreamingCoroutineClient::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) = runBlocking {
    Loggers.init()

    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
    try {
      val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)

      val requests = flow {
        emit(echoRequest { this.message = "world" })
        emit(echoRequest { this.message = "welt" })
        emit(echoRequest { this.message = "monde" })
      }
      val responses = stub.bidirectionalStreamingEcho(requests)
      responses.collect { response ->
        logger.info("next response: ${response.message}")
      }
    } catch (e: StatusRuntimeException) {
      logger.warning("RPC error: ${e.status}")
    } finally {
      channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
  }
}
