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
                StringBuilder summary = new StringBuilder();

                @Override public void onNext(EchoRequest req) {
                    summary.append(req.getMessage()).append(" ");
                }

                @Override public void onError(Throwable t) { t.printStackTrace(); }

                @Override public void onCompleted() {
                    responseObserver.onNext(EchoResponse.newBuilder()
                        .setMessage("received: " + summary.toString().trim())
                        .build());
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
