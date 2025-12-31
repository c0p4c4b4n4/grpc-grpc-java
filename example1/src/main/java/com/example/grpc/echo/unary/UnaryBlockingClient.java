package com.example.grpc.echo.unary;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();
            var response = blockingStub.unaryEcho(request);
            logger.info("response: " + response.getMessage());
        } catch (StatusRuntimeException e) {
            logger.warning("error: " + e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
