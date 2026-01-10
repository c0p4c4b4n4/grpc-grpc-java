package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import java.util.concurrent.TimeUnit

suspend fun main(args: Array<String>) {
  val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
  try {
    val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)
    val request = echoRequest { this.message = "world" }
    val response = stub.unaryEcho(request)
    println("response: ${response.message}")
  } catch (e: StatusRuntimeException) {
    println("RPC error: ${e.status}")
  } finally {
    channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
  }
}
