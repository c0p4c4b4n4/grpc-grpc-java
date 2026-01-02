package com.example.grpc.features.keepalive;

import com.example.grpc.Delays;
import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnaryBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.initWithGrpcLogs();

        var channel = Grpc.newChannelBuilderForAddress("localhost", 50051, InsecureChannelCredentials.create())
            .keepAliveTime(10, TimeUnit.SECONDS)
            .keepAliveTimeout(1, TimeUnit.SECONDS)
            .keepAliveWithoutCalls(true)
            .build();

        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();
            var response = blockingStub.unaryEcho(request);
            logger.log(Level.INFO, "response: {0}", response.getMessage());

            Delays.sleep(30);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
