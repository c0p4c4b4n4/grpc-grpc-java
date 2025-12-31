package com.example.grpc.echo.unary;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.Servers;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class UnaryServer {

    private static final Logger logger = Logger.getLogger(UnaryServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.info("request: "+ request.getMessage());
            var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
