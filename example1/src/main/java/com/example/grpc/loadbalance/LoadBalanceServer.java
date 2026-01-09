package com.example.grpc.loadbalance;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class /*TODO*/ LoadBalanceServer {

    private static final Logger logger = Logger.getLogger(LoadBalanceServer.class.getName());

    private List<Server> servers;

    public static void main(String[] args) throws IOException, InterruptedException {
        final LoadBalanceServer server = new LoadBalanceServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        servers = new ArrayList<>();
        for (int port : Settings.SERVER_PORTS) {
            servers.add(
                ServerBuilder.forPort(port)
                    .addService(new GreeterImpl(port))
                    .build()
                    .start());
            logger.info("Server started, listening on " + port);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                LoadBalanceServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        for (Server server : servers) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        for (Server server : servers) {
            server.awaitTermination();
        }
    }

    static class /*TODO*/ GreeterImpl extends EchoServiceGrpc.EchoServiceImplBase {

        int port;

        public GreeterImpl(int port) {
            this.port = port;
        }

        @Override
        public void unaryEcho(EchoRequest req, StreamObserver<EchoResponse> responseObserver) {
            EchoResponse reply = EchoResponse.newBuilder().setMessage("Echo " + req.getMessage() + " from server<" + this.port + ">").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
