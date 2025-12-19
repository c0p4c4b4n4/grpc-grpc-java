package io.grpc.examples.echo2;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Echo3Client {

    public static void main(String[] args) throws Exception {
        String target = "localhost:50051";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
            .build();

        CountDownLatch finishLatch = new CountDownLatch(1);

        EchoServiceGrpc.EchoServiceFutureStub futureStub = EchoServiceGrpc.newFutureStub(channel);
        ListenableFuture<EchoResponse> responseFuture = futureStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
        Futures.addCallback(responseFuture, new FutureCallback<EchoResponse>() {
            @Override
            public void onSuccess(EchoResponse result) {
                System.out.println(result.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                finishLatch.countDown();
            }
        }, MoreExecutors.directExecutor());

        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            System.err.println("Calls did not finish within timeout.");
        }

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
