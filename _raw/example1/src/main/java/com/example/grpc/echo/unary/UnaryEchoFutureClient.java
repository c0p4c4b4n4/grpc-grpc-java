package com.example.grpc.echo.unary;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoFutureClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoFutureClient.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceFutureStub futureStub = EchoServiceGrpc.newFutureStub(channel);
        ListenableFuture<EchoResponse> responseFuture = futureStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
        Futures.addCallback(responseFuture, new FutureCallback<EchoResponse>() {
            @Override
            public void onSuccess(EchoResponse response) {
                logger.info("response received: " + response.getMessage());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warning("error: " + Status.fromThrowable(t));
            }
        }, MoreExecutors.directExecutor());

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}

