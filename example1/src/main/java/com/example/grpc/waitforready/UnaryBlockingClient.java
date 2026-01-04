package com.example.grpc.waitforready;

import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Deadline;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ UnaryBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel)
                .withWaitForReady()
                .withDeadline(Deadline.after(30, TimeUnit.SECONDS));
            var request = EchoRequest.newBuilder().setMessage("world").build();
            var response = blockingStub.unaryEcho(request);
            logger.log(Level.INFO, "response: {0}", response.getMessage());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
