package com.example.grpc;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class /*TODO*/ Servers {

    public static void start(BindableService bindableService, Logger logger) throws IOException, InterruptedException {
        Loggers.init();

        var port = 50051;
        var server = ServerBuilder
            .forPort(port)
            .addService(bindableService)
            .build()
            .start();

        logger.log(Level.INFO, "server started, listening on {0,number,#}", port);

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
