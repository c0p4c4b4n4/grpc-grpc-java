package com.example.grpc.cancellation

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.logging.Logger

object CancellationServerStreamingServer {
  private val logger = Logger.getLogger(CancellationServerStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override fun serverStreamingEcho(request: EchoRequest): Flow<EchoResponse> = flow {
      val name = request.message
      logger.info("request: $name")

      try {
        for (i in 0..9) {
          emit(echoResponse { message = "hello $name $i" })

          delay(1000L)
        }
      } catch (e: CancellationException) {
        logger.info("server received cancellation")
        throw e
      }
    }
  }
}
