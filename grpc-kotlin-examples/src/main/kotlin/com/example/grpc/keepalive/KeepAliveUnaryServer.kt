package com.example.grpc.keepalive

import com.example.grpc.EchoRequest
import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.Loggers
import com.example.grpc.Servers
import com.example.grpc.echoResponse
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit

object KeepAliveUnaryServer {

  @JvmStatic
  fun main(args: Array<String>) {
    Loggers.initIoGrpc()

    val serverBuilder = ServerBuilder
      .forPort(50051)
      .addService(EchoServiceImpl())
      .keepAliveTime(5, TimeUnit.SECONDS)
      .keepAliveTimeout(1, TimeUnit.SECONDS)
      .permitKeepAliveTime(5, TimeUnit.SECONDS)
      .permitKeepAliveWithoutCalls(true)
      .maxConnectionIdle(15, TimeUnit.SECONDS)
      .maxConnectionAge(30, TimeUnit.SECONDS)
      .maxConnectionAgeGrace(5, TimeUnit.SECONDS)

    Servers.start(serverBuilder)
  }

  private class EchoServiceImpl : EchoServiceGrpcKt.EchoServiceCoroutineImplBase() {
    override suspend fun unaryEcho(request: EchoRequest) =
      echoResponse { message = "hello ${request.message}" }
  }
}
