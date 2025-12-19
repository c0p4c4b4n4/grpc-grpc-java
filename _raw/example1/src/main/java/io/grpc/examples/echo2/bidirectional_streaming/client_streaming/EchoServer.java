package io.grpc.examples.echo2.bidirectional_streaming.client_streaming;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
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
        public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<EchoRequest>() {
                @Override
                public void onNext(EchoRequest request) {
                    // Business logic: Echo back immediately or process
                    EchoResponse response = EchoResponse.newBuilder().setMessage("server next: " + request.getMessage()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("server error: " + t);
                }

                @Override
                public void onCompleted() {
                    System.out.println("server completed");
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
