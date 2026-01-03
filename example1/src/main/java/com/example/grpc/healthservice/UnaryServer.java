package com.example.grpc.healthservice;

import com.example.grpc.Delays;
import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Status;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class UnaryServer {

    private static final Logger logger = Logger.getLogger(UnaryServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        Loggers.init();

        var port = 50051;
        var health = new HealthStatusManager();

        var server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
            .addService(new EchoServiceImpl(health))
            .addService(health.getHealthService())
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

        health.setStatus("", ServingStatus.SERVING);

        server.awaitTermination();
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        private final HealthStatusManager health;
        private final AtomicBoolean isServing = new AtomicBoolean(true);

        public EchoServiceImpl(HealthStatusManager health) {
            this.health = health;
        }

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            if (!isServing.get()) {
                responseObserver.onError(Status.INTERNAL.withDescription("not serving right now").asRuntimeException());
                return;
            }

            if (shouldServe(request)) {
                var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                logger.warning("short message received, will not serve for 10 seconds");
                health.setStatus("", ServingStatus.NOT_SERVING);

                isServing.set(false);

                new Thread(() -> {
                    Delays.sleep(10);
                    isServing.set(true);

                    logger.info("continue to serve");
                    health.setStatus("", ServingStatus.SERVING);
                }).start();

                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("paused by short message").asRuntimeException());
            }
        }

        private boolean shouldServe(EchoRequest request) {
            return isServing.get() && request.getMessage().length() >= 5;
        }
    }
}
