package com.example.grpc.echo.streaming.bidirectional;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ BidirectionalStreamingClient {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        var asyncStub = EchoServiceGrpc.newStub(channel);

        var requestObserver = asyncStub.bidirectionalStreamingEcho(new StreamObserver<>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.log(Level.INFO, "next: {0}", response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
            }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("world").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("welt").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("monde").build());
        requestObserver.onCompleted();

        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
}
