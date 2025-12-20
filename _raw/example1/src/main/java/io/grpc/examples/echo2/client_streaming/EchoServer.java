package io.grpc.examples.echo2.client_streaming;

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
        public StreamObserver<EchoRequest> clientStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<EchoRequest>() {
                StringBuilder result = new StringBuilder();

                @Override
                public void onNext(EchoRequest request) {
                    String message = request.getMessage();
                    System.out.println("server next: " + message);
                    result.append(message).append(" ");
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("server error: " + t);
                }

                @Override
                public void onCompleted() {
                    System.out.println("server completed: " + result.toString().trim());
                    responseObserver.onNext(EchoResponse.newBuilder()
                        .setMessage("server completed: " + result.toString().trim())
                        .build());
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
