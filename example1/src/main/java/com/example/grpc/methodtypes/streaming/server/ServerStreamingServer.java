package com.example.grpc.methodtypes.streaming.server;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Servers;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStreamingServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl());
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());

            responseObserver.onNext(EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build());
            responseObserver.onNext(EchoResponse.newBuilder().setMessage("guten tag " + request.getMessage()).build());
            responseObserver.onNext(EchoResponse.newBuilder().setMessage("bonjour " + request.getMessage()).build());

            responseObserver.onCompleted();
        }
    }
}
