package io.grpc.examples.echo2.bidirectional_streaming.client_streaming;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class Echo2Client1 {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);

        StreamObserver<EchoRequest> requestObserver = asyncStub.clientStreamingEcho(new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                System.out.println("client next: " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("client error: " + t);
            }

            @Override
            public void onCompleted() {
                System.out.println("client completed");
            }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("one").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("two").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("three").build());
        requestObserver.onCompleted();

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
