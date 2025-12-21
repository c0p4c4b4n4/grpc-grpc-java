package io.grpc.examples.echo2.unary;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;

import java.util.concurrent.TimeUnit;

public class UnaryEchoFutureClient {

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceFutureStub futureStub = EchoServiceGrpc.newFutureStub(channel);
        ListenableFuture<EchoResponse> responseFuture = futureStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
        Futures.addCallback(responseFuture, new FutureCallback<EchoResponse>() {
            @Override
            public void onSuccess(EchoResponse response) {
                System.out.println("success: " + response.getMessage());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("error: " + t);
            }
        }, MoreExecutors.directExecutor());

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}

