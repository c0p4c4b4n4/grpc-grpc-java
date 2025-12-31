package com.example.grpc.features.healthservice;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HealthServiceServer {

    private static final Logger logger = Logger.getLogger(HealthServiceServer.class.getName());

    private Server server;
    private HealthStatusManager health;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        health = new HealthStatusManager();
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
            .addService(new EchoServiceImpl())
            .addService(health.getHealthService())
            .build()
            .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HealthServiceServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });

        health.setStatus("", ServingStatus.SERVING);
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tH:%1$tM:%1$tS %4$s %2$s: %5$s%6$s%n");

        final HealthServiceServer server = new HealthServiceServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        boolean isServing = true;

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            if (!isServing) {
                responseObserver.onError(Status.INTERNAL.withDescription("Not Serving right now").asRuntimeException());
                return;
            }

            if (isNameLongEnough(request)) {
                EchoResponse reply = EchoResponse.newBuilder().setMessage("Echo " + request.getMessage()).build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } else {
                logger.warning("Tiny message received, throwing a temper tantrum");
                health.setStatus("", ServingStatus.NOT_SERVING);
                isServing = false;

                // In 10 seconds set it back to serving
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        isServing = true;
                        health.setStatus("", ServingStatus.SERVING);
                        logger.info("tantrum complete");
                    }
                }).start();
                responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Offended by short name").asRuntimeException());
            }
        }

        private boolean isNameLongEnough(EchoRequest req) {
            return isServing && req.getMessage().length() >= 5;
        }
    }
}
