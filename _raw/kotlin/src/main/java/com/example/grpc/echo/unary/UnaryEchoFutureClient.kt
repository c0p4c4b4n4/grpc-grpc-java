package com.example.grpc.echo.unary

import com.example.grpc.echo.EchoRequest
import com.example.grpc.echo.EchoResponse
import com.example.grpc.echo.EchoServiceGrpc
import com.example.grpc.echo.Logging
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.grpc.Status
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object UnaryEchoFutureClient {
    private val logger: Logger = Logger.getLogger(UnaryEchoFutureClient::class.java.getName())

    @kotlin.Throws(Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        Logging.init()

        val channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build()

        val futureStub = EchoServiceGrpc.newFutureStub(channel)
        val request = EchoRequest.newBuilder().setMessage("world").build()
        val responseFuture = futureStub.unaryEcho(request)

        val latch = CountDownLatch(1)
        Futures.addCallback<EchoResponse?>(responseFuture, object : FutureCallback<EchoResponse?> {
            override fun onSuccess(response: EchoResponse) {
                logger.info("result: " + response.getMessage())
                latch.countDown()
            }

            override fun onFailure(t: Throwable) {
                logger.warning("error: " + Status.fromThrowable(t))
                latch.countDown()
            }
        }, MoreExecutors.directExecutor())

        latch.await()
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
}

