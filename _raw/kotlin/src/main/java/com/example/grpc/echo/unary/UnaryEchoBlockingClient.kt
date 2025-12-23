package com.example.grpc.echo.unary

import com.example.grpc.echo.EchoRequest
import com.example.grpc.echo.EchoServiceGrpc
import com.example.grpc.echo.Logging
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object UnaryEchoBlockingClient {
    private val logger: Logger = Logger.getLogger(UnaryEchoBlockingClient::class.java.getName())

    @kotlin.Throws(Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        Logging.init()

        val channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build()
        try {
            val blockingStub = EchoServiceGrpc.newBlockingStub(channel)
            val request = EchoRequest.newBuilder().setMessage("world").build()
            val response = blockingStub.unaryEcho(request)
            logger.info("response: " + response.getMessage())
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
        }
    }
}
