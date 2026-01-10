package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Status
import io.grpc.StatusRuntimeException
import java.io.Closeable
import java.util.concurrent.TimeUnit

class UnaryBlockingClient(private val channel: ManagedChannel) : Closeable {
  private val stub: EchoServiceGrpcKt.EchoServiceCoroutineStub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)

  suspend fun greet(name: String) {
    val request = echoRequest { this.message = name }
    val response = stub.unaryEcho(request)
    println("response: ${response.message}")
  }

  override fun close() {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }
}

suspend fun main(args: Array<String>) {
  val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
  try {
    val stub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)
    val request = echoRequest { this.message = "world" }
    val response = stub.unaryEcho(request)
    println("response: ${response.message}")
  } catch (e: StatusRuntimeException) {
    when (e.status.code) {
      Status.Code.NOT_FOUND -> println("Resource not found")
      Status.Code.DEADLINE_EXCEEDED -> println("Request timed out")
      else -> println("RPC error: ${e.status}")
    }
  } finally {
    channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
  }
}
