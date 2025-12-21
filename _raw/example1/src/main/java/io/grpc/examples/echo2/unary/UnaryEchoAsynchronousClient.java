package io.grpc.examples.echo2.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class UnaryEchoAsynchronousClient {

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        asyncStub.unaryEcho(request, new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse response) {
                System.out.println("next: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error: " + t);
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
