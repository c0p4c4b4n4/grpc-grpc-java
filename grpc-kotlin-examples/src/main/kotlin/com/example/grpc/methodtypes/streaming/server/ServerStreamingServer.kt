package com.example.grpc.methodtypes.streaming.server

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.logging.Logger

object ServerStreamingServer {
  private val logger = Logger.getLogger(ServerStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
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