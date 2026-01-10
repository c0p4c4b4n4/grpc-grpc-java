package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class UnaryBlockingClient {
  private val logger = Logger.getLogger(UnaryBlockingClient::class.java.name)

  suspend fun main(args: Array<String>) {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
    try {
      val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)
      val request = echoRequest { this.message = "world" }
      val response = stub.unaryEcho(request)
      logger.info("response: ${response.message}")
    } catch (e: StatusRuntimeException) {
      logger.warning("RPC error: ${e.status}")
    } finally {
      channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
  }
}