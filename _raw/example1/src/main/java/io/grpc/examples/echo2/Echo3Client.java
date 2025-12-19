package io.grpc.examples.echo2;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Echo3Client {

    public static void main(String[] args) throws Exception {
        String target = "localhost:50051";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
            .build();

        CountDownLatch finishLatch = new CountDownLatch(1);

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        asyncStub.unaryEcho(
            EchoRequest.newBuilder().setMessage("world").build(),
            new StreamObserver<EchoResponse>() {
            @Override public void onNext(EchoResponse value) { System.out.println(value.getMessage()); }
            @Override public void onError(Throwable t) { finishLatch.countDown(); }
            @Override public void onCompleted() {finishLatch.countDown();}
        });

        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            System.err.println("Calls did not finish within timeout.");
        }

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
