package com.example.grpc.features.retrying;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetryingHelloWorldClient {

    private static final Logger logger = Logger.getLogger(RetryingHelloWorldClient.class.getName());

    private final ManagedChannel channel;
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;
    private final AtomicInteger totalRpcs = new AtomicInteger();
    private final AtomicInteger failedRpcs = new AtomicInteger();

    protected Map<String, ?> getRetryingServiceConfig() {
        // 1. Define the "name" entry
        Map<String, Object> name = new HashMap<>();
        name.put("service", "com.example.grpc.echo.EchoService");
        name.put("method", "unaryEcho");

        // 2. Define the "retryPolicy"
        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", 5.0); // gRPC expects numbers as doubles when using Maps
        retryPolicy.put("initialBackoff", "0.5s");
        retryPolicy.put("maxBackoff", "30s");
        retryPolicy.put("backoffMultiplier", 2.0);
        retryPolicy.put("retryableStatusCodes", List.of("UNAVAILABLE"));

        // 3. Define the "methodConfig" entry
        Map<String, Object> methodConfigEntry = new HashMap<>();
        methodConfigEntry.put("name", List.of(name));
        methodConfigEntry.put("retryPolicy", retryPolicy);

        // 4. Wrap in the root "methodConfig" list
        Map<String, Object> serviceConfig = new HashMap<>();
        serviceConfig.put("methodConfig", List.of(methodConfigEntry));

        return serviceConfig;
    }


    public RetryingHelloWorldClient(String host, int port) {
        ManagedChannelBuilder<?> channelBuilder = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create());

        Map<String, ?> serviceConfig = getRetryingServiceConfig();
        logger.info("Client started with retrying configuration: " + serviceConfig);
        channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();

        channel = channelBuilder.build();
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
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
            logger.log(Level.INFO, "Greeting: {0}", new Object[]{response.getMessage()});
        } else {
            logger.log(Level.INFO, "RPC failed: {0}", new Object[]{statusRuntimeException.getStatus()});
        }
    }

    private void printSummary() {
        logger.log(
            Level.INFO,
            "\n\nTotal RPCs sent: {0}. Total RPCs failed: {1}\n",
            new Object[]{
                totalRpcs.get(), failedRpcs.get()});
    }

    public static void main(String[] args) throws Exception {
        RetryingHelloWorldClient client = new RetryingHelloWorldClient("localhost", 50051);
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
