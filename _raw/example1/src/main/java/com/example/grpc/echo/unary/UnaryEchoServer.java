package com.example.grpc.echo.unary;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import com.example.grpc.echo.Shutdown;
import io.grpc.stub.StreamObserver;

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
