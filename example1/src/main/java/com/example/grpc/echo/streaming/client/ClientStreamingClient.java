package com.example.grpc.echo.streaming.client;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientStreamingClient {

    private static final Logger logger = Logger.getLogger(ClientStreamingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        var asyncStub = EchoServiceGrpc.newStub(channel);

        var requestObserver = asyncStub.clientStreamingEcho(new StreamObserver<>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.info("next: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("error: " + Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
            }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("one").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("two").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("three").build());
        requestObserver.onCompleted();

        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
}
