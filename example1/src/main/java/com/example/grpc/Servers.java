package com.example.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Servers {

    public static void start(BindableService bindableService, Logger logger) throws IOException, InterruptedException {
        Loggers.init();

        int port = 50051;
        Server server = ServerBuilder.forPort(port)
            .addService(bindableService)
            .build()
            .start();

        logger.log(Level.INFO, "server started, listening on {0}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("server is shutting down");
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("server shutdown was interrupted");
                server.shutdownNow();
            }
            System.err.println("server has been shut down");
        }));

        server.awaitTermination();
    }
}
