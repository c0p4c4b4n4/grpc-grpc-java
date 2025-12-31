package com.example.grpc.features.loadbalance;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadBalanceClient {

    private static final Logger logger = Logger.getLogger(LoadBalanceClient.class.getName());

    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;

    public LoadBalanceClient(Channel channel) {
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
    }

    public void greet(String name) {
        EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
        EchoResponse response;
        try {
            response = blockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "error: {0}", e.getStatus());
            return;
        }
        logger.info("response: " + response.getMessage());
    }


    public static void main(String[] args) throws Exception {
        NameResolverRegistry.getDefaultRegistry().register(new ExampleNameResolverProvider());
        String target = String.format("%s:///%s", Settings.SCHEME, Settings.SERVICE_NAME);

        useFirctPickPolicy(target);
        useRoundRobinPolicy(target);
    }

    private static void useFirctPickPolicy(String target) throws InterruptedException {
        logger.info("Use default first_pick load balance policy");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        try {
            LoadBalanceClient client = new LoadBalanceClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void useRoundRobinPolicy(String target) throws InterruptedException {
        logger.info("Change to round_robin policy");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        try {
            LoadBalanceClient client = new LoadBalanceClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
