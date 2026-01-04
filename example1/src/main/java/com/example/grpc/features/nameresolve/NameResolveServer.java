package com.example.grpc.features.nameresolve;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class /*TODO*/ NameResolveServer {

    private static final Logger logger = Logger.getLogger(NameResolveServer.class.getName());

    static public final int serverCount = 3;
    static public final int startPort = 50051;
    private Server[] servers;

    public static void main(String[] args) throws IOException, InterruptedException {
        final NameResolveServer server = new NameResolveServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        servers = new Server[serverCount];
        for (int i = 0; i < serverCount; i++) {
            int port = startPort + i;
            servers[i] = ServerBuilder.forPort(port)
                .addService(new GreeterImpl(port))
                .build()
                .start();
            logger.info("Server started, listening on " + port);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                NameResolveServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        for (int i = 0; i < serverCount; i++) {
            if (servers[i] != null) {
                servers[i].shutdown().awaitTermination(30, TimeUnit.SECONDS);
            }
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        for (int i = 0; i < serverCount; i++) {
            if (servers[i] != null) {
                servers[i].awaitTermination();
            }
        }
    }

    static class /*TODO*/ GreeterImpl extends EchoServiceGrpc.EchoServiceImplBase {

        int port;

        public GreeterImpl(int port) {
            this.port = port;
        }

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            EchoResponse reply = EchoResponse.newBuilder().setMessage("Hello " + request.getMessage() + " from server<" + this.port + ">").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
