package io.grpc.examples.echo2;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class EchoServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
            .addService(new EchoServiceImpl())
            .build()
            .start();
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void unaryEcho(EchoRequest req, StreamObserver<EchoResponse> responseObserver) {
            EchoResponse reply = EchoResponse.newBuilder().setMessage("Hello " + req.getMessage()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
