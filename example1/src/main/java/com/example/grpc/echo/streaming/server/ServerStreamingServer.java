package com.example.grpc.echo.streaming.server;

import com.example.grpc.Servers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ ServerStreamingServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    private static class /*TODO*/ EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());

            var response1 = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
            responseObserver.onNext(response1);
            var response2 = EchoResponse.newBuilder().setMessage("guten tag " + request.getMessage()).build();
            responseObserver.onNext(response2);
            var response3 = EchoResponse.newBuilder().setMessage("bonjour " + request.getMessage()).build();
            responseObserver.onNext(response3);

            responseObserver.onCompleted();
        }
    }
}
