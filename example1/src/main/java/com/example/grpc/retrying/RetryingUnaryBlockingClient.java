package com.example.grpc.retrying;

import com.example.grpc.Constants;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
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

    public static void main(String[] args) throws Exception {
        Loggers.init();

        var enableRetries = !Boolean.parseBoolean(System.getenv("EXAMPLE_GRPC_DISABLE_RETRYING"));
        var channel = buildChannel(enableRetries);
        var blockingStub = EchoServiceGrpc.newBlockingStub(channel);

        final AtomicInteger totalCalls = new AtomicInteger();
        final AtomicInteger failedCalls = new AtomicInteger();

        var executor = new ForkJoinPool();
        for (var name : Constants.getNames()) {
            executor.execute(() -> {
                try {
                    var request = EchoRequest.newBuilder().setMessage(name).build();
                    var response = blockingStub.unaryEcho(request);
                    logger.log(Level.INFO, "response: {0}", response.getMessage());
                } catch (StatusRuntimeException e) {
                    logger.log(Level.INFO, "error: {0}", e.getStatus());
                    failedCalls.incrementAndGet();
                }

                totalCalls.incrementAndGet();
            });
        }
        executor.awaitQuiescence(120, TimeUnit.SECONDS);
        executor.shutdown();

        logger.log(Level.INFO, "retrying: {0}, calls sent: {1}, calls failed: {2}",
            new Object[]{enableRetries ? "enabled" : "disabled", totalCalls.get(), failedCalls.get()});

        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }

    private static ManagedChannel buildChannel(boolean enableRetries) {
        var channelBuilder = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext();
        if (enableRetries) {
            var serviceConfig = getRetryingServiceConfig();
            logger.info("client started with service configuration: " + serviceConfig);
            channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();
        }
        var channel = channelBuilder.build();
        return channel;
    }

    private static Map<String, ?> getRetryingServiceConfig() {
        return Map.of(
            "methodConfig", List.of(
                Map.of(
                    "name", List.of(
                        Map.of(
                            "service", "example.grpc.EchoService",
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
}
