package com.example.grpc.deadline

import com.example.grpc.EchoRequest
import com.example.grpc.EchoResponse
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.util.logging.Logger

object DeadlineServerStreamingServer {
  private val logger = Logger.getLogger(DeadlineServerStreamingServer::class.java.name)

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override fun serverStreamingEcho(request: EchoRequest): Flow<EchoResponse> {
      val name = request.message
      logger.info("request: $name")

      return flow {
        for (i in 0..9) {
          val response = echoResponse { message = "hello $name $i" }
          logger.info("response: ${response.message}")
          emit(response)

          delay(i * 1000L)
        }
      }.onCompletion { cause ->
        if (cause != null) {
          logger.warning("stream cancelled or deadline exceeded: ${cause.message}")
        } else {
          logger.info("stream completed successfully")
        }
      }
    }
  }
}
