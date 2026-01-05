package com.example.grpc.echo.unary

import com.example.grpc.EchoRequest

object  /*TODO*/ UnaryBlockingClient {
    private val logger: java.util.logging.Logger =
        java.util.logging.Logger.getLogger(UnaryBlockingClient::class.java.getName())

    @kotlin.Throws(java.lang.Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: kotlin.Array<kotlin.String>) {
        Loggers.init()

        val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()

        try {
            val blockingStub: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                EchoServiceGrpc.newBlockingStub(channel)
            val request: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                EchoRequest.newBuilder().setMessage("world").build()
            val response: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                blockingStub.unaryEcho(request)
            UnaryBlockingClient.logger.log(java.util.logging.Level.INFO, "response: {0}", response.getMessage())
        } catch (e: StatusRuntimeException) {
            UnaryBlockingClient.logger.log(java.util.logging.Level.WARNING, "RPC error: {0}", e.getStatus())
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS)
        }
    }
}
