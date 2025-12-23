package com.example.grpc.echo.unary

import com.example.grpc.echo.EchoRequest
import com.example.grpc.echo.EchoResponse
import com.example.grpc.echo.EchoServiceGrpc
import com.example.grpc.echo.Logging
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object UnaryEchoAsynchronousClient {
    private val logger: Logger = Logger.getLogger(UnaryEchoAsynchronousClient::class.java.getName())

    @kotlin.Throws(Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        Logging.init()

        val channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build()

        val asyncStub = EchoServiceGrpc.newStub(channel)
        val request = EchoRequest.newBuilder().setMessage("world").build()

        val latch = CountDownLatch(1)
        asyncStub.unaryEcho(request, object : StreamObserver<EchoResponse?> {
            override fun onNext(response: EchoResponse) {
                logger.info("next: " + response.getMessage())
            }

            override fun onError(t: Throwable) {
                logger.warning("error: " + Status.fromThrowable(t))
                latch.countDown()
            }

            override fun onCompleted() {
                logger.info("completed")
                latch.countDown()
            }
        })

        latch.await()
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
}
