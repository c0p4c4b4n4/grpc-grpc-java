package com.example.grpc.features.retrying;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetryingUnaryBlockingClient {

    private static final Logger logger = Logger.getLogger(RetryingUnaryBlockingClient.class.getName());

    private final boolean enableRetries;
    private final ManagedChannel channel;
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;
    private final AtomicInteger totalRpcs = new AtomicInteger();
    private final AtomicInteger failedRpcs = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        Loggers.init();

        boolean enableRetries = !Boolean.parseBoolean(System.getenv("EXAMPLE_GRPC_DISABLE_RETRYING"));
        RetryingUnaryBlockingClient client = new RetryingUnaryBlockingClient("localhost", 50051, enableRetries);

        ForkJoinPool executor = new ForkJoinPool();
        for (int i = 0; i < 50; i++) {
            String userId = "user" + i;
            executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        client.unaryEcho(userId);
                    }
                });
        }
        executor.awaitQuiescence(120, TimeUnit.SECONDS);
        executor.shutdown();

        client.printSummary();
        client.shutdown();
    }

    private RetryingUnaryBlockingClient(String host, int port, boolean enableRetries) {
        ManagedChannelBuilder<?> channelBuilder = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create());
        if (enableRetries) {
            Map<String, ?> serviceConfig = getRetryingServiceConfig();
            logger.info("client started with service configuration: " + serviceConfig);
            channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();
        }
        channel = channelBuilder.build();
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        this.enableRetries = enableRetries;
    }

    private Map<String, ?> getRetryingServiceConfig() {
        return Map.of(
            "methodConfig", List.of(
                Map.of(
                    "name", List.of(
                        Map.of(
                            "service", "example.grpc.echo.EchoService",
                            "method", "UnaryEcho"
                        )
                    ),
                    "retryPolicy", Map.of(
                        "maxAttempts", 5.0,
                        "initialBackoff", "0.5s",
                        "maxBackoff", "30s",
                        "backoffMultiplier", 2.0,
                        "retryableStatusCodes", List.of("UNAVAILABLE")
                    )
                )
            )
        );
    }

    private void unaryEcho(String message) {
        EchoRequest request = EchoRequest.newBuilder().setMessage(message).build();
        EchoResponse response = null;

        StatusRuntimeException statusRuntimeException = null;
        try {
            response = blockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            failedRpcs.incrementAndGet();
            statusRuntimeException = e;
        }

        totalRpcs.incrementAndGet();

        if (statusRuntimeException == null) {
            logger.log(Level.INFO, "response: {0}", response.getMessage());
        } else {
            logger.log(Level.INFO, "error: {0}", statusRuntimeException.getStatus());
        }
    }

    private void printSummary() {
        logger.log(Level.INFO, "retrying: {0}, calls sent: {1}, calls failed: {2}\n",
            new Object[]{enableRetries ? "enabled" : "disabled", totalRpcs.get(), failedRpcs.get()});
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
    }
}
