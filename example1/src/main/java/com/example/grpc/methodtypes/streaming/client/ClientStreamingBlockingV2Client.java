package com.example.grpc.methodtypes.streaming.client;

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

@io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
public class ClientStreamingBlockingV2Client {

    private static final Logger logger = Logger.getLogger(ClientStreamingBlockingV2Client.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        // 1. Create the V2 Blocking Stub
        EchoServiceGrpc.EchoServiceBlockingV2Stub blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);

        // 2. Initiate the client streaming call
        // This returns a BlockingClientCall object instead of a StreamObserver
        BlockingClientCall<EchoRequest, EchoResponse> call = blockingStub.echoClientStream();

        try {
            // 3. Stream multiple messages to the server
            String[] messages = {"Hello", "from", "BlockingV2", "Stub"};
            for (String msg : messages) {
                System.out.println("Sending: " + msg);
                call.write(EchoRequest.newBuilder().setMessage(msg).build());
            }

            // 4. Signal that the client is done sending
            call.closeSend();

            // 5. Read the single response from the server
            // This blocks until the server calls onCompleted/onNext
            EchoResponse response = call.read();
            System.out.println("Server Summary: " + response.getSummary());

        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
    }
}
