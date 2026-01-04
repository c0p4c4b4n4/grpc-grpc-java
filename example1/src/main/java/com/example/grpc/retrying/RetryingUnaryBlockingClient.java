package com.example.grpc.retrying;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ RetryingUnaryBlockingClient {

    private static final Logger logger = Logger.getLogger(RetryingUnaryBlockingClient.class.getName());

    private final ManagedChannel channel;
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;
    private final AtomicInteger totalCalls = new AtomicInteger();
    private final AtomicInteger failedCalls = new AtomicInteger();

    private RetryingUnaryBlockingClient(String host, int port, boolean enableRetries) {
        var channelBuilder = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create());
        if (enableRetries) {
            var serviceConfig = getRetryingServiceConfig();
            logger.info("client started with service configuration: " + serviceConfig);
            channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();
        }
        channel = channelBuilder.build();
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var enableRetries = !Boolean.parseBoolean(System.getenv("EXAMPLE_GRPC_DISABLE_RETRYING"));
        var client = new RetryingUnaryBlockingClient("localhost", 50051, enableRetries);

        var executor = new ForkJoinPool();
        for (var i = 0; i < 50; i++) {
            var userId = "user" + i;
            executor.execute(() -> client.unaryEcho(userId));
        }
        executor.awaitQuiescence(120, TimeUnit.SECONDS);
        executor.shutdown();

        client.printSummary(enableRetries);
        client.shutdown();
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
        try {
            var request = EchoRequest.newBuilder().setMessage(message).build();
            var response = blockingStub.unaryEcho(request);
            logger.log(Level.INFO, "response: {0}", response.getMessage());
        } catch (StatusRuntimeException e) {
            failedCalls.incrementAndGet();
            logger.log(Level.INFO, "error: {0}", e.getStatus());
        }

        totalCalls.incrementAndGet();
    }

    private void printSummary(boolean enableRetries) {
        logger.log(Level.INFO, "retrying: {0}, calls sent: {1}, calls failed: {2}\n",
            new Object[]{enableRetries ? "enabled" : "disabled", totalCalls.get(), failedCalls.get()});
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
    }
}
