package com.example.grpc.echo.server_streaming;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import com.example.grpc.echo.Shutdown;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ServerStreamingEchoServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        Server server = ServerBuilder.forPort(50051)
            .addService(
                new EchoServiceImpl()
            )
            .build()
            .start();

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.info("request: " + request.getMessage());
            for (int i = 1; i <= 3; i++) {
                String value = "hello " + request.getMessage() + " " + i;
                EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
