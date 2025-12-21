package io.grpc.examples.echo2.unary;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

public class UnaryEchoServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
            .addService(new EchoServiceImpl())
            .build()
            .start();
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
