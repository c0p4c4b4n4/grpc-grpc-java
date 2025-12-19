package io.grpc.examples.echo2;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class Echo1Client {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        try {
            // Create the modernized Blocking V2 Stub
            EchoServiceGrpc.EchoServiceBlockingV2Stub blockingV2Stub =
                EchoServiceGrpc.newBlockingV2Stub(channel);

            // Create request
            EchoRequest request = EchoRequest.newBuilder()
                .setMessage("Hello from Blocking V2")
                .build();

            // Execute the unary call (this blocks until response is received)
            System.out.println("Sending Unary request via V2 stub...");
            EchoResponse response = blockingV2Stub.unaryEcho(request);

            System.out.println("Response received: " + response.getMessage());

        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        } finally {
            channel.shutdown();
        }
    }

    public static void main2(String[] args) throws Exception {
        String target = "localhost:50051";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
            .build();

        EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        EchoResponse response = blockingStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
        System.out.println(response.getMessage());
    }
}
