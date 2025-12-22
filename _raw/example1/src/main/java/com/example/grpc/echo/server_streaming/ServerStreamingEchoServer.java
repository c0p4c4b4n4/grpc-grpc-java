package com.example.grpc.echo.server_streaming;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import com.example.grpc.echo.Shutdown;
import io.grpc.stub.StreamObserver;

public class ServerStreamingEchoServer {

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
//            logger.info("Received server streaming echo request: " + request.getMessage());
            for (int i = 1; i <= 5; i++) {
                String value = "echo [" + i + "]: " + request.getMessage();
                EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
