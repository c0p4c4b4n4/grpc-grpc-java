package com.example.grpc.keepalive;

import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeepAliveUnaryServer {

    private static final Logger logger = Logger.getLogger(KeepAliveUnaryServer.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.initWithGrpcLogs();

        var port = 50051;
        var server = ServerBuilder
            .forPort(port)
            .addService(new EchoServiceImpl())
            .keepAliveTime(5, TimeUnit.SECONDS)
            .keepAliveTimeout(1, TimeUnit.SECONDS)
            .permitKeepAliveTime(5, TimeUnit.SECONDS)
            .permitKeepAliveWithoutCalls(true)
            .maxConnectionIdle(15, TimeUnit.SECONDS)
            .maxConnectionAge(30, TimeUnit.SECONDS)
            .maxConnectionAgeGrace(5, TimeUnit.SECONDS)
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

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());
            var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
