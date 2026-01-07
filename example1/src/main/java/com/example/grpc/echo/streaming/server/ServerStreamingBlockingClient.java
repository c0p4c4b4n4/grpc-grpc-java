package com.example.grpc.echo.streaming.server;

import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ ServerStreamingBlockingClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingBlockingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();
            var responses = blockingStub.serverStreamingEcho(request);

            while (responses.hasNext()) {
                logger.log(Level.INFO, "next response: {0}", responses.next().getMessage());
            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
