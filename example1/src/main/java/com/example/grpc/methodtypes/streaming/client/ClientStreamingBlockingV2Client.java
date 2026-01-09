package com.example.grpc.methodtypes.streaming.client;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.BlockingClientCall;
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
        var blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);
        var blockingClientCall = blockingStub.clientStreamingEcho();

        try {
            for (String message : new String[]{"world", "welt", "monde"}) {
                var request = EchoRequest.newBuilder().setMessage(message).build();
                logger.log(Level.INFO, "request: {0}", request.getMessage());
                blockingClientCall.write(request);
            }

            // 4. Signal that the client is done sending
            blockingClientCall.halfClose();

            EchoResponse response = blockingClientCall.read();
            System.out.println("Server Summary: " + response.getMessage());
        } catch (StatusException e) {
            logger.log(Level.WARNING, "RPC error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
