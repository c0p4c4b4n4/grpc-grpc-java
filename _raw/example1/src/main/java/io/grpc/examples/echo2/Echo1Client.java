package io.grpc.examples.echo2;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;

public class Echo1Client {

    public static void main(String[] args) {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
            .build();

        try {
            EchoServiceGrpc.EchoServiceBlockingV2Stub blockingV2Stub = EchoServiceGrpc.newBlockingV2Stub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            EchoResponse response = blockingV2Stub.unaryEcho(request);
            System.out.println(response.getMessage());

        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        } catch (StatusException e) {
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
