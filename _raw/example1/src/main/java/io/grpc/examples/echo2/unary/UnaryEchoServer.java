package io.grpc.examples.echo2.unary;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoServer {

    private static final Logger logger = Logger.getLogger(UnaryEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

        Server server = ServerBuilder.forPort(50051)
            .addService(
                new EchoServiceGrpc.EchoServiceImplBase() {
                    @Override
                    public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
                        logger.info("request received: " + request.getMessage());
                        EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    }
                }
            )
            .build()
            .start();

        logger.info("server started");

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

        server.awaitTermination();
    }
}
