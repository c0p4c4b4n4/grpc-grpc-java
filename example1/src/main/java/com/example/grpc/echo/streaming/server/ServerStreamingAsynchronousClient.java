package com.example.grpc.echo.streaming.server;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerStreamingAsynchronousClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingAsynchronousClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        var asyncStub = EchoServiceGrpc.newStub(channel);
        var request = EchoRequest.newBuilder().setMessage("world").build();

        var latch = new CountDownLatch(1);
        asyncStub.serverStreamingEcho(request, new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.info("next: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("error: " + Status.fromThrowable(t));
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
                latch.countDown();
            }
        });

        latch.await();
        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
}
