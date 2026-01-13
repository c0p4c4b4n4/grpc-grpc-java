package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers
import com.example.grpc.echoResponse

object WaitForReadyUnaryServer {

  @JvmStatic
  fun main(args: Array<String>) {
    Servers.start(EchoServiceImpl())
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) =
      echoResponse { message = "hello ${request.message}" }
  }
}
