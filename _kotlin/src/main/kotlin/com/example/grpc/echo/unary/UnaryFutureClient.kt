package com.example.grpc.echo.unary

import com.example.grpc.EchoRequest

object  /*TODO*/ UnaryFutureClient {
    private val logger: java.util.logging.Logger =
        java.util.logging.Logger.getLogger(UnaryFutureClient::class.java.getName())

    @kotlin.Throws(java.lang.Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: kotlin.Array<kotlin.String>) {
        Loggers.init()

        val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()

        val futureStub: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
            EchoServiceGrpc.newFutureStub(channel)
        val request: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
            EchoRequest.newBuilder().setMessage("world").build()
        val responseFuture: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
            futureStub.unaryEcho(request)

        val done: CountDownLatch = CountDownLatch(1)
        Futures.addCallback<kotlin.Any?>(responseFuture, object : FutureCallback<kotlin.Any?> {
            public override fun onSuccess(response: EchoResponse) {
                UnaryFutureClient.logger.info("result: " + response.getMessage())
                done.countDown()
            }

            override fun onFailure(t: Throwable) {
                UnaryFutureClient.logger.log(
                    java.util.logging.Level.WARNING,
                    "error: {0}",
                    io.grpc.Status.fromThrowable(t)
                )
                done.countDown()
            }
        }, MoreExecutors.directExecutor())

        done.await()
        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS)
    }
}

