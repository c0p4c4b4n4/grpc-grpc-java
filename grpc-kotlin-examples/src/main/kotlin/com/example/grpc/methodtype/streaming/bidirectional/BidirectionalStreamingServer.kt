package com.example.grpc.methodtype.streaming.bidirectional

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
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
        val name = request.message
        logger.info("next request: $name")

        emit(echoResponse { message = "hello $name" })
        emit(echoResponse { message = "guten tag $name" })
        emit(echoResponse { message = "bonjour $name" })
      }
    }
  }
}
