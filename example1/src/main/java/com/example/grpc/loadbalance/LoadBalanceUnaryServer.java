package com.example.grpc.loadbalance;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class LoadBalanceUnaryServer {

    private static final Logger logger = Logger.getLogger(LoadBalanceUnaryServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        Loggers.init();

        List<Server> servers = new ArrayList<>();
        for (int port : Settings.SERVER_PORTS) {
            servers.add(
                ServerBuilder.forPort(port)
                    .addService(new GreeterImpl(port))
                    .build()
                    .start()
            );
            logger.info("Server started, listening on " + port);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                for (Server server : servers) {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));

        for (Server server : servers) {
            server.awaitTermination();
        }
    }

    static class GreeterImpl extends EchoServiceGrpc.EchoServiceImplBase {

        final int port;

        public GreeterImpl(int port) {
            this.port = port;
        }

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage() + " from server<" + this.port + ">").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
