package io.grpc.examples.echo2.client_streaming;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusException;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;

public class Echo1Client {

    public static void main(String[] args) {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        try {
            EchoServiceGrpc.EchoServiceBlockingV2Stub blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            EchoResponse response = blockingStub.unaryEcho(request);
            System.out.println(response.getMessage());
        } catch (StatusException e) {
            System.err.println("RPC failed: " + e.getStatus());
        } finally {
            channel.shutdown();
        }
    }

    public static void main2(String[] args) throws Exception {
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
