package com.example.grpc.methodtypes.unary

import com.example.grpc.EchoServiceGrpcKt
import com.example.grpc.echoRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt.GreeterCoroutineStub
import io.grpc.examples.helloworld.helloRequest
import java.io.Closeable
import java.util.concurrent.TimeUnit

class UnaryBlockingClient(private val channel: ManagedChannel) : Closeable {
  private val stub: EchoServiceGrpcKt.EchoServiceCoroutineStub = EchoServiceGrpcKt.EchoServiceCoroutineStub(channel)

  suspend fun greet(name: String) {
    val request = echoRequest { this.message = name }
    val response = stub.unaryEcho(request)
    println("Received: ${response.message}")
  }

  override fun close() {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }
}

/** Greeter, uses first argument as name to greet if present; greets "world" otherwise. */
suspend fun main(args: Array<String>) {
  val port = 50051

  val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

  val client = UnaryBlockingClient(channel)

  val user = "world"
  client.greet(user)
}
