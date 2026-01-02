package com.example.grpc.features.keepalive;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryServer {

    private static final Logger logger = Logger.getLogger(UnaryServer.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.initWithGrpcLogs();

        int port = 50051;
        Server server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
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

        logger.info("server started, listening on " + port);

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
