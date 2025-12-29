package com.example.grpc.echo;

import io.grpc.Server;

import java.util.concurrent.TimeUnit;

public class Shutdown {

    public static void init(Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("server is shutting down");
            try {
                server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("server shutdown was interrupted");
                server.shutdownNow();
            }
            System.err.println("server has been shut down");
        }));
    }
}
