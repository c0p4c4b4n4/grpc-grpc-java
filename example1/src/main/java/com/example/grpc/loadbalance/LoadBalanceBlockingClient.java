package com.example.grpc.loadbalance;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ LoadBalanceBlockingClient {

    private static final Logger logger = Logger.getLogger(LoadBalanceBlockingClient.class.getName());

    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;

    public LoadBalanceBlockingClient(Channel channel) {
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        NameResolverRegistry.getDefaultRegistry().register(new ExampleNameResolverProvider());
        String target = String.format("%s:///%s", Settings.SCHEME, Settings.SERVICE_NAME);

        useFirstPickLoadBalancingPolicy(target);
        useRoundRobinLoadBalancingPolicy(target);
    }

    private static void useFirstPickLoadBalancingPolicy(String target) throws InterruptedException {
        logger.info("use default first_pick load balance policy");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();
        try {
            LoadBalanceBlockingClient client = new LoadBalanceBlockingClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void useRoundRobinLoadBalancingPolicy(String target) throws InterruptedException {
        logger.info("use round_robin load balance policy");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build();
        try {
            LoadBalanceBlockingClient client = new LoadBalanceBlockingClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void greet(String name) {
        EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
        EchoResponse response;
        try {
            response = blockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC error: {0}", e.getStatus());
            return;
        }
        logger.log(Level.INFO, "response: {0}", response.getMessage());
    }
}
