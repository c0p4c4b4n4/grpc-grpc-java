package com.example.grpc.headers

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse

object HeadersUnaryServer {

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) =
      echoResponse { message = "hello ${request.message}" }
  }
}
