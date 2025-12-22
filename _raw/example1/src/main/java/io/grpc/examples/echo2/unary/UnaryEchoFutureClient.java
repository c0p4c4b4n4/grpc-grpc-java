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
import java.util.logging.Logger;

public class UnaryEchoFutureClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoFutureClient.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
        
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

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}

