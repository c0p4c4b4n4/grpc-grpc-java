package io.grpc.examples.echo2;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class Echo3Client {

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceFutureStub futureStub = EchoServiceGrpc.newFutureStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        ListenableFuture<EchoResponse> responseFuture = futureStub.unaryEcho(request);
        Futures.addCallback(responseFuture, new FutureCallback<EchoResponse>() {
            @Override
            public void onSuccess(EchoResponse result) {
                System.out.println(result.getMessage());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("error: " + t);
            }
        }, MoreExecutors.directExecutor());

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
