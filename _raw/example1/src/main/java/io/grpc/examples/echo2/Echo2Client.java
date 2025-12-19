package io.grpc.examples.echo2;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class Echo2Client {

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        asyncStub.unaryEcho(
            request,
            new StreamObserver<EchoResponse>() {
                @Override
                public void onNext(EchoResponse value) {
                    System.out.println(value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error: " + t);
                }

                @Override
                public void onCompleted() {
                    System.out.println("completed");
                }
            });

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
