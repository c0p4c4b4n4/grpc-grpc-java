package com.example.grpc.echo.client_streaming;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientStreamingEchoClient {

    private static final Logger logger = Logger.getLogger(ClientStreamingEchoClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);

        StreamObserver<EchoRequest> requestObserver = asyncStub.clientStreamingEcho(new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                System.out.println("client next: " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("error: " + Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                System.out.println("client completed");
            }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("one").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("two").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("three").build());
        requestObserver.onCompleted();

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
