package com.example.grpc.echo.streaming.server;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Loggers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerStreamingBlockingClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingBlockingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        try {
            EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            Iterator<EchoResponse> responses = blockingStub.serverStreamingEcho(request);

            while (responses.hasNext()) {
                logger.info("response: " + responses.next().getMessage());
            }
        } catch (StatusRuntimeException e) {
            logger.warning("error: " + e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
