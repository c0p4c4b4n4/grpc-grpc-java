package com.example.grpc.features.retrying;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.io.InputStreamReader;
import java.util.HashMap;
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

    protected Map<String, ?> getRetryingServiceConfig() {
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

    public RetryingHelloWorldClient(String host, int port, boolean enableRetries) {
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

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
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
            logger.log(Level.INFO,"Greeting: {0}", new Object[]{response.getMessage()});
        } else {
            logger.log(Level.INFO,"RPC failed: {0}", new Object[]{statusRuntimeException.getStatus()});
        }
    }

    private void printSummary() {
        logger.log(
            Level.INFO,
            "\n\nTotal RPCs sent: {0}. Total RPCs failed: {1}\n",
            new Object[]{
                totalRpcs.get(), failedRpcs.get()});

        if (enableRetries) {
            logger.log(
                Level.INFO,
                "Retrying enabled. To disable retries, run the client with environment variable {0}=true.",
                ENV_DISABLE_RETRYING);
        } else {
            logger.log(
                Level.INFO,
                "Retrying disabled. To enable retries, unset environment variable {0} and then run the client.",
                ENV_DISABLE_RETRYING);
        }
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
                        client.greet(userId);
                    }
                });
        }
        executor.awaitQuiescence(100, TimeUnit.SECONDS);
        executor.shutdown();

        client.printSummary();
        client.shutdown();
    }
}
