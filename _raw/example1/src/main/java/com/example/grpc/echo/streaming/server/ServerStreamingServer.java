package com.example.grpc.echo.streaming.server;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Servers;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ServerStreamingServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.info("request: " + request.getMessage());
            for (int i = 1; i <= 7; i++) {
                String value = "hello " + request.getMessage() + " " + i;
                EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
