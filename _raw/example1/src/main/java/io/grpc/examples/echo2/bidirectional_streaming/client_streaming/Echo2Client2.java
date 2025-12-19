package io.grpc.examples.echo2.bidirectional_streaming.client_streaming;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Echo2Client2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);

        CompletableFuture<String> responseFuture = new CompletableFuture<>();

        StreamObserver<EchoRequest> requestObserver = asyncStub.clientStreamingEcho(new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                responseFuture.complete(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                responseFuture.completeExceptionally(t);
            }

            @Override
            public void onCompleted() { /* No-op: server already sent data in onNext */ }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("one").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("two").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("three").build());
        requestObserver.onCompleted();

        String value = responseFuture.get(5, TimeUnit.SECONDS);
        System.out.println("client result: " + value);

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
