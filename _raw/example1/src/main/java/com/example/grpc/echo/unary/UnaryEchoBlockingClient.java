package com.example.grpc.echo.unary;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        try {
            EchoServiceGrpc.EchoServiceBlockingV2Stub blockingStub = EchoServiceGrpc.newBlockingV2Stub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            EchoResponse response = blockingStub.unaryEcho(request);
            logger.info("response: " + response.getMessage());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
