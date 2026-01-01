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

public class RetryingHelloWorldClient {

    static final String ENV_DISABLE_RETRYING = "DISABLE_RETRYING_IN_RETRYING_EXAMPLE";

    private static final Logger logger = Logger.getLogger(RetryingHelloWorldClient.class.getName());

    private final boolean enableRetries;
    private final ManagedChannel channel;
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;
    private final AtomicInteger totalRpcs = new AtomicInteger();
    private final AtomicInteger failedRpcs = new AtomicInteger();

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

    private RetryingHelloWorldClient(String host, int port, boolean enableRetries) {
        ManagedChannelBuilder<?> channelBuilder            = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create());
        if (enableRetries) {
            Map<String, ?> serviceConfig = getRetryingServiceConfig();
            logger.info("Client started with retrying configuration: " + serviceConfig);
            channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();
        }
        channel = channelBuilder.build();
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        this.enableRetries = enableRetries;
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
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
            logger.log(Level.INFO,"response: {0}", response.getMessage());
        } else {
            logger.log(Level.INFO,"error: {0}", statusRuntimeException.getStatus());
        }
    }

    private void printSummary() {
        logger.log(
            Level.INFO,
            "\n\nRetrying: {0}. Total RPCs sent: {1}. Total RPCs failed: {2}\n",
            new Object[]{  enableRetries ?"enabled" :"disabled",   totalRpcs.get(), failedRpcs.get()});

//        if (enableRetries) {
//            logger.log(
//                Level.INFO,
//                "Retrying enabled. To disable retries, run the client with environment variable {0}=true.",
//                ENV_DISABLE_RETRYING);
//        } else {
//            logger.log(
//                Level.INFO,
//                "Retrying disabled. To enable retries, unset environment variable {0} and then run the client.",
//                ENV_DISABLE_RETRYING);
//        }
    }

    public static void main(String[] args) throws Exception {
        Loggers.init();

        boolean enableRetries = !Boolean.parseBoolean(System.getenv(ENV_DISABLE_RETRYING));
        RetryingHelloWorldClient client = new RetryingHelloWorldClient("localhost", 50051, enableRetries);

        ForkJoinPool executor = new ForkJoinPool();
        for (int i = 0; i < 50; i++) {
            final String userId = "user" + i;
            executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        client.unaryEcho(userId);
                    }
                });
        }
        executor.awaitQuiescence(100, TimeUnit.SECONDS);
        executor.shutdown();

        client.printSummary();
        client.shutdown();
    }
}
