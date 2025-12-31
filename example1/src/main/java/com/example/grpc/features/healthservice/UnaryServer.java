package com.example.grpc.features.healthservice;

import com.example.grpc.Delays;
import com.example.grpc.Loggable;
import com.example.grpc.Loggers;
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

public class UnaryServer extends Loggable {

    private Server server;
    private HealthStatusManager health;

    private void start() throws IOException {
        int port = 50051;
        health = new HealthStatusManager();

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
            .addService(new EchoServiceImpl())
            .addService(health.getHealthService())
            .build()
            .start();

        logger.info("server started, listening on "+ port);

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

        health.setStatus("", ServingStatus.SERVING);
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Loggers.init();

        UnaryServer server = new UnaryServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        boolean serving = true;

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            if (!serving) {
                responseObserver.onError(Status.INTERNAL.withDescription("not serving right now").asRuntimeException());
                return;
            }

            if (shouldHandle(request)) {
                EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                logger.warning("short message received, will not serve for 10 seconds");
                health.setStatus("", ServingStatus.NOT_SERVING);

                serving = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Delays.sleep(10);
                        serving = true;

                        health.setStatus("", ServingStatus.SERVING);
                        logger.info("continue to serve");
                    }
                }).start();

                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("offended by short message").asRuntimeException());
            }
        }

        private boolean shouldHandle(EchoRequest request) {
            return serving && request.getMessage().length() >= 5;
        }
    }
}
