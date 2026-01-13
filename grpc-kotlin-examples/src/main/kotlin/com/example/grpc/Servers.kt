package com.example.grpc

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object Servers {
  private val logger = Logger.getLogger(Servers::class.java.name)

  fun start(serverBuilder: ServerBuilder<*>) {
    Loggers.init()

    val server: Server = serverBuilder
      .build()
      .start()

    logger.info("server started, listening on ${server.port}")

    Runtime.getRuntime().addShutdownHook(
      Thread {
        System.err.println("server is shutting down")
        try {
          server.shutdown().awaitTermination(10, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
          server.shutdownNow()
        }
        System.err.println("server has been shut down")
      }
    )

    server.awaitTermination()
  }

  fun start(bindableService: BindableService) {
    Loggers.init()

    val server = ServerBuilder
      .forPort(50051)
      .addService(bindableService)
      .build()
      .start()

    logger.info("server started, listening on ${server.port}")

    Runtime.getRuntime().addShutdownHook(
      Thread {
        System.err.println("server is shutting down")
        try {
          server.shutdown().awaitTermination(10, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
          server.shutdownNow()
        }
        System.err.println("server has been shut down")
      }
    )

    server.awaitTermination()
  }
}
