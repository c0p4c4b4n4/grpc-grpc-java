package com.example.grpc.methodtypes.streaming.bidirectional;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BidirectionalStreamingBlockingV2Client {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingBlockingV2Client.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();
        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        var blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);

// 2. Initiate the call. This returns a call object for bi-di interaction.
        try (var call = blockingStub.bidirectionalStreamingEcho()) {

            // 3. Send requests using .write()
            call.write(EchoRequest.newBuilder().setMessage("world").build());
            call.write(EchoRequest.newBuilder().setMessage("welt").build());
            call.write(EchoRequest.newBuilder().setMessage("monde").build());

            // Signal that no more requests will be sent
            call.halfClose();

            // 4. Read responses synchronously until the stream is exhausted
            // Note: read() blocks until a message arrives or the stream closes
            EchoResponse response;
            while ((response = call.read()) != null) {
                logger.log(Level.INFO, "next response: {0}", response.getMessage());
            }

            logger.info("completed");

        } catch (StatusException e) {
            // V2 stubs use checked StatusException for streaming/client calls
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
