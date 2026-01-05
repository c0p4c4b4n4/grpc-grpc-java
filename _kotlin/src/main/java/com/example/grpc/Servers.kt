package com.example.grpc

import io.grpc.BindableService
import io.grpc.ServerBuilder
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

object  /*TODO*/ Servers {
    @kotlin.Throws(IOException::class, InterruptedException::class)
    fun start(bindableService: BindableService?, logger: Logger) {
        Loggers.init()

        val port = 50051
        val server = ServerBuilder
            .forPort(port)
            .addService(bindableService)
            .build()
            .start()

        logger.log(Level.INFO, "server started, listening on {0,number,#}", port)

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            System.err.println("server is shutting down")
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                System.err.println("server shutdown was interrupted")
                server.shutdownNow()
            }
            System.err.println("server has been shut down")
        }))

        server.awaitTermination()
    }
}
