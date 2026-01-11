package com.example.grpc.methodtypes.streaming.client

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import kotlinx.coroutines.flow.Flow
import java.util.logging.Logger

object ClientStreamingServer {
  private val logger = Logger.getLogger(ClientStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun clientStreamingEcho(requests: Flow<EchoRequest>): EchoResponse {
      val responses = mutableListOf<String>()

      requests.collect { request ->
        logger.info("next request: ${request.message}")
        responses.add("hello ${request.message}")
      }

      return echoResponse { message = responses.joinToString(", ") }
    }
  }
}