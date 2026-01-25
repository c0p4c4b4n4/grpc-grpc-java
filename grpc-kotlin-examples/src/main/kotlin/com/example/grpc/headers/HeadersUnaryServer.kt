package com.example.grpc.headers

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Servers.start
import com.example.grpc.echoResponse
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptors

object HeadersUnaryServer {

  @JvmStatic
  fun main(args: Array<String>) {
    val serverBuilder = ServerBuilder
      .forPort(50051)
      .addService(ServerInterceptors.intercept(EchoServiceImpl(), HeadersServerInterceptor()))

    start(serverBuilder)
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) =
      echoResponse { message = "hello ${request.message}" }
  }
}
