package io.grpc.examples.echo2.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;

public class UnaryEchoBlockingClient {

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        try {
            EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            EchoResponse response = blockingStub.unaryEcho(request);
            System.out.println(response.getMessage());
        } finally {
            channel.shutdown();
        }
    }
}
