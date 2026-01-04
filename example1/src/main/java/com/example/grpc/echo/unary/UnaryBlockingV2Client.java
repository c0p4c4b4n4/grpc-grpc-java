package com.example.grpc.echo.unary;

import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ UnaryBlockingV2Client {

    private static final Logger logger = Logger.getLogger(UnaryBlockingV2Client.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        try {
            var blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();
            var response = blockingStub.unaryEcho(request);
            logger.log(Level.INFO, "response: {0}", response.getMessage());
        } catch (StatusException e) {
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
