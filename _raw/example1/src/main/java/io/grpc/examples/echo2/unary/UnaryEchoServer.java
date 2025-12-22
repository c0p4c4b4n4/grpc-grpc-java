package io.grpc.examples.echo2.unary;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.echo2.Logging;
import io.grpc.examples.echo2.Shutdown;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoServer {

    private static final Logger logger = Logger.getLogger(UnaryEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

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

        Shutdown.init(server);
        server.awaitTermination();
    }
}
