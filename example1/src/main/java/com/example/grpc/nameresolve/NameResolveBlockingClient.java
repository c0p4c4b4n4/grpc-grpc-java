package com.example.grpc.nameresolve;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Loggers;
import com.example.grpc.loadbalance.ExampleNameResolverProvider;
import com.example.grpc.loadbalance.Settings;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NameResolveBlockingClient {

    private static final Logger logger = Logger.getLogger(NameResolveBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        NameResolverRegistry.getDefaultRegistry().register(new ExampleNameResolverProvider());
        var target = String.format("%s:///%s", com.example.grpc.loadbalance.Settings.SCHEME, Settings.SERVICE_NAME);

        useFirstPickPolicy(target);
        useRoundRobinPolicy(target);
    }

    private static void useFirstPickPolicy(String target) throws InterruptedException {
        logger.info("use default first_pick load balance policy");
        var channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();
        try {
            for (int i = 0; i < 5; i++) {
                unaryEcho(channel, "name" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private static void useRoundRobinPolicy(String target) throws InterruptedException {
        logger.info("use round_robin load balance policy");
        var channel = ManagedChannelBuilder.forTarget(target)
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build();
        try {
            for (int i = 0; i < 5; i++) {
                unaryEcho(channel, "name" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private static void unaryEcho(ManagedChannel channel, String name) {
        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage(name).build();
            var response = blockingStub.unaryEcho(request);
            logger.log(Level.INFO, "response: {0}", response.getMessage());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC error: {0}", e.getStatus());
        }
    }
}
