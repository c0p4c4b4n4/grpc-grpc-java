package io.grpc.examples.echo2;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class Echo1Client {

    public static void main(String[] args) throws Exception {
        String target = "localhost:50051";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
            .build();

        EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        EchoResponse response = blockingStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
        System.out.println(response.getMessage());
    }
}
