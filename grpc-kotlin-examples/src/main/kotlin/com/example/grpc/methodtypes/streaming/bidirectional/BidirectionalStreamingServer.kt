package com.example.grpc.methodtypes.streaming.bidirectional

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.logging.Logger

object BidirectionalStreamingServer {
  private val logger = Logger.getLogger(BidirectionalStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
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