package com.example.grpc.header;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import com.example.grpc.Servers;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeaderUnaryServer {

    private static final Logger logger = Logger.getLogger(HeaderUnaryServer.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var port = 50051;
        var serverBuilder = ServerBuilder
            .forPort(port)
            .addService(ServerInterceptors.intercept(new EchoServiceImpl(), new HeaderServerInterceptor()));

        Servers.start(port, serverBuilder);
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());
            var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
