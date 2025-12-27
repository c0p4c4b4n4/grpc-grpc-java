package com.example.grpc.echo.server_streaming;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStreamingEchoBlockingClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingEchoBlockingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        try {
            EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            Iterator<EchoResponse> responses = blockingStub.serverStreamingEcho(request);

            while (responses.hasNext()) {
                logger.log(Level.INFO, "response: {0}", responses.next().getMessage());
            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
