package com.example.grpc.echo.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoAsynchronousClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoAsynchronousClient.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        asyncStub.unaryEcho(request, new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.info("next: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("client error: {0}" + Status.fromThrowable(t));

                logger.info("error: " + t);
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
            }
        });

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
