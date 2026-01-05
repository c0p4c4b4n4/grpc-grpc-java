package com.example.grpc.echo.unary

import com.example.grpc.EchoRequest

object  /*TODO*/ UnaryAsynchronousClient {
    private val logger: java.util.logging.Logger =
        java.util.logging.Logger.getLogger(UnaryAsynchronousClient::class.java.getName())

    @kotlin.Throws(java.lang.Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: kotlin.Array<kotlin.String>) {
        Loggers.init()

        val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()

        val asyncStub: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
            EchoServiceGrpc.newStub(channel)
        val request: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
            EchoRequest.newBuilder().setMessage("world").build()

        val done: CountDownLatch = CountDownLatch(1)
        asyncStub.unaryEcho(request, object : StreamObserver<V?> {
            public override fun onNext(response: EchoResponse) {
                UnaryAsynchronousClient.logger.log(java.util.logging.Level.INFO, "next: {0}", response.getMessage())
            }

            override fun onError(t: Throwable) {
                UnaryAsynchronousClient.logger.log(
                    java.util.logging.Level.WARNING,
                    "error: {0}",
                    io.grpc.Status.fromThrowable(t)
                )
                done.countDown()
            }

            override fun onCompleted() {
                UnaryAsynchronousClient.logger.info("completed")
                done.countDown()
            }
        })

        done.await()
        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS)
    }
}
