package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object UnaryServer {

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) =
      echoResponse { message = "hello ${request.message}" }
  }
}