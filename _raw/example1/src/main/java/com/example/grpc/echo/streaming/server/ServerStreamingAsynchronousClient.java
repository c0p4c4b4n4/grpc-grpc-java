package com.example.grpc.echo.streaming.server;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStreamingAsynchronousClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingAsynchronousClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();

        CountDownLatch latch = new CountDownLatch(1);
        asyncStub.serverStreamingEcho(request, new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.log(Level.INFO, "next: {0}", response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
                latch.countDown();
            }
        });

        latch.await();
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
