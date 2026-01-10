package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
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
  val client = UnaryBlockingClient(channel)
  client.greet("world")
}
