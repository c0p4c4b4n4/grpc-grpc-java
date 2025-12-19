package io.grpc.examples.echo2.server_streaming;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Echo1Client {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        Iterator<EchoResponse> responses = blockingStub.serverStreamingEcho(request);

        while (responses.hasNext()) {
            System.out.println("received: " + responses.next().getMessage());
        }
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
