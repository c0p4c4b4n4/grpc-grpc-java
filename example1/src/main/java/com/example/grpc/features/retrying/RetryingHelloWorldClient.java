package com.example.grpc.features.retrying;

import static java.nio.charset.StandardCharsets.UTF_8;

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

/**
 * A client that requests a greeting from the {@link RetryingHelloWorldServer} with a retrying policy.
 */
public class RetryingHelloWorldClient {
    static final String ENV_DISABLE_RETRYING = "DISABLE_RETRYING_IN_RETRYING_EXAMPLE";

    private static final Logger logger = Logger.getLogger(RetryingHelloWorldClient.class.getName());

    private final boolean enableRetries;
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
//      return Map.of()
//    return new Gson()
//        .fromJson(
//            new JsonReader(
//                new InputStreamReader(
//                    RetryingHelloWorldClient.class.getResourceAsStream(
//                        "retrying_service_config.json"),
//                    UTF_8)),
//            Map.class);
    }

/*
{
  "methodConfig": [
    {
      "name": [
        {
          "service": "helloworld.Greeter",
          "method": "SayHello"
        }
      ],

      "retryPolicy": {
        "maxAttempts": 5,
        "initialBackoff": "0.5s",
        "maxBackoff": "30s",
        "backoffMultiplier": 2,
        "retryableStatusCodes": [
          "UNAVAILABLE"
        ]
      }
    }
  ]
}
 */


    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public RetryingHelloWorldClient(String host, int port, boolean enableRetries) {

        ManagedChannelBuilder<?> channelBuilder
            = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create());
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

    /**
     * Say hello to server in a blocking unary call.
     */
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
        boolean enableRetries = !Boolean.parseBoolean(System.getenv(ENV_DISABLE_RETRYING));
        final RetryingHelloWorldClient client = new RetryingHelloWorldClient("localhost", 50051, enableRetries);
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
