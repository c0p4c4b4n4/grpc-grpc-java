package com.example.grpc.nameresolve;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NameResolveUnaryServer {

    private static final Logger logger = Logger.getLogger(NameResolveUnaryServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        Loggers.init();

        var servers = new ArrayList<Server>();
        for (int port : Settings.SERVER_PORTS) {
            servers.add(
                ServerBuilder.forPort(port)
                    .addService(new EchoServiceImpl(port))
                    .build()
                    .start()
            );
            logger.log(Level.INFO, "server started, listening on {0,number,#}", port);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("servers are shutting down");
            for (Server server : servers) {
                try {
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("server shutdown was interrupted");
                    server.shutdownNow();
                }
            }
            System.err.println("servers have been shut down");
        }));

        for (Server server : servers) {
            server.awaitTermination();
        }
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        private final int port;

        EchoServiceImpl(int port) {
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
