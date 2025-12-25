package com.example.grpc.echo.server_streaming;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import com.example.grpc.echo.Shutdown;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStreamingEchoServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        int port = 50051;
        Server server = ServerBuilder.forPort(port)
            .addService(new EchoServiceImpl())
            .build()
            .start();

        logger.log(Level.INFO, "server started, listening on {0}", port);

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());
            for (int i = 1; i <= 7; i++) {
                String value = "hello " + request.getMessage() + " " + i;
                EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
