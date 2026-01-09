package com.example.grpc.nameresolve;

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

public class /*TODO*/ NameResolveClient {

    private static final Logger logger = Logger.getLogger(NameResolveClient.class.getName());
    private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;

    public NameResolveClient(Channel channel) {
        blockingStub = EchoServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        NameResolverRegistry.getDefaultRegistry().register(new ExampleNameResolverProvider());

        logger.info("Use default DNS resolver");
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051")
            .usePlaintext()
            .build();
        try {
            NameResolveClient client = new NameResolveClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }

        logger.info("Change to use example name resolver");
        /*
          Dial to "example:///resolver.example.grpc.io", use {@link ExampleNameResolver} to create connection
          "resolver.example.grpc.io" is converted to {@link java.net.URI.path}
         */
        channel = ManagedChannelBuilder.forTarget("example:///lb.example.grpc.io")
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build();
        try {
            NameResolveClient client = new NameResolveClient(channel);
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
        logger.info("Greeting: " + response.getMessage());
    }
}
