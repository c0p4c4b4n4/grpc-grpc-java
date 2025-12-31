package com.example.grpc.features.healthservice;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.health.v1.HealthGrpc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HealthServiceClient {
    private static final Logger logger = Logger.getLogger(HealthServiceClient.class.getName());

    private final EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub;

    private final HealthGrpc.HealthStub healthStub;
    private final HealthGrpc.HealthBlockingStub healthBlockingStub;

    private final HealthCheckRequest healthRequest;

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    public HealthServiceClient(Channel channel) {
        echoBlockingStub = EchoServiceGrpc.newBlockingStub(channel);
        healthStub = HealthGrpc.newStub(channel);
        healthBlockingStub = HealthGrpc.newBlockingStub(channel);
        healthRequest = HealthCheckRequest.getDefaultInstance();
        LoadBalancerProvider roundRobin = LoadBalancerRegistry
            .getDefaultRegistry()
            .getProvider("round_robin");

    }

    private ServingStatus checkHealth(String prefix) {
        HealthCheckResponse response = healthBlockingStub.check(healthRequest);
        logger.info(prefix + ", current health is: " + response.getStatus());
        return response.getStatus();
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
        EchoResponse response;
        try {
            response = echoBlockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }


    private static void runTest(String target, String[] users)
        throws InterruptedException {
        ManagedChannelBuilder<?> builder =
            Grpc.newChannelBuilder(target, InsecureChannelCredentials.create());

        // Round Robin, when a healthCheckConfig is present in the default service configuration, runs
        // a watch on the health service and when picking an endpoint will
        // consider a transport to a server whose service is not in SERVING state to be unavailable.
        // Since we only have a single server we are connecting to, then the load balancer will
        // return an error without sending the RPC.
        if (false) {
            builder = builder
                .defaultLoadBalancingPolicy("round_robin")
                .defaultServiceConfig(generateHealthConfig(""));
        }

        ManagedChannel channel = builder.build();

        System.out.println("\nDoing test with" + (false ? "" : "out")
            + " the Round Robin load balancer\n");

        try {
            HealthServiceClient client = new HealthServiceClient(channel);
            if (!false) {
                client.checkHealth("Before call");
            }
            client.greet(users[0]);
            if (!false) {
                client.checkHealth("After user " + users[0]);
            }

            for (String user : users) {
                client.greet(user);
                Thread.sleep(100); // Since the health update is asynchronous give it time to propagate
            }

            if (!false) {
                client.checkHealth("After all users");
                Thread.sleep(10000);
                client.checkHealth("After 10 second wait");
            } else {
                Thread.sleep(10000);
            }
            client.greet("Larry");
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static Map<String, Object> generateHealthConfig(String serviceName) {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> serviceMap = new HashMap<>();

        config.put("healthCheckConfig", serviceMap);
        serviceMap.put("serviceName", serviceName);
        return config;
    }

    /**
     * Uses a server with both a greet service and the health service.
     * If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     * This has an example of using the health service directly through the unary call
     * <a href="https://github.com/grpc/grpc-java/blob/master/services/src/main/proto/grpc/health/v1/health.proto">check</a>
     * to get the current health.  It also utilizes the health of the server's greet service
     * indirectly through the round robin load balancer, which uses the streaming rpc
     * <strong>watch</strong> (you can see how it is done in
     * {@link  io.grpc.protobuf.services.HealthCheckingLoadBalancerFactory}).
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tH:%1$tM:%1$tS %4$s %2$s: %5$s%6$s%n");

        String[] users = {"world", "foo", "I am Grut"};
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [target [name] [name] ...]");
                System.err.println("");
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.err.println("  name    The names you wish to be greeted by. Defaults to " + Arrays.toString(users));
                System.exit(1);
            }
            target = args[0];
        }
        if (args.length > 1) {
            users = new String[args.length - 1];
            for (int i = 0; i < users.length; i++) {
                users[i] = args[i + 1];
            }
        }

        runTest(target, users);


    }
}
