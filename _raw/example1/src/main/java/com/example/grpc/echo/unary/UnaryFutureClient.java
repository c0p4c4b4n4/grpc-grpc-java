package com.example.grpc.echo.unary;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Loggers;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryFutureClient {

    private static final Logger logger = Logger.getLogger(UnaryFutureClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        var futureStub = EchoServiceGrpc.newFutureStub(channel);
        var request = EchoRequest.newBuilder().setMessage("world").build();
        var responseFuture = futureStub.unaryEcho(request);

        var latch = new CountDownLatch(1);
        Futures.addCallback(responseFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(EchoResponse response) {
                logger.info("result: " + response.getMessage());
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warning("error: " + Status.fromThrowable(t));
                latch.countDown();
            }
        }, MoreExecutors.directExecutor());

        latch.await();
        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
}

