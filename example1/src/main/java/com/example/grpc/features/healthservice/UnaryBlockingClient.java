package com.example.grpc.features.healthservice;

import com.example.grpc.Loggable;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
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

public class UnaryBlockingClient extends Loggable {

    private final EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub;

    private final HealthGrpc.HealthStub healthStub;
    private final HealthGrpc.HealthBlockingStub healthBlockingStub;

    private final HealthCheckRequest healthRequest;

    public UnaryBlockingClient(Channel channel) {
        echoBlockingStub = EchoServiceGrpc.newBlockingStub(channel);
        healthStub = HealthGrpc.newStub(channel);
        healthBlockingStub = HealthGrpc.newBlockingStub(channel);
        healthRequest = HealthCheckRequest.getDefaultInstance();
    }

    private ServingStatus checkHealth(String prefix) {
        HealthCheckResponse response = healthBlockingStub.check(healthRequest);
        logger.info(prefix + ", current health is: " + response.getStatus());
        return response.getStatus();
    }

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


    private static void runTest(String target, String[] users) throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

        try {
            UnaryBlockingClient client = new UnaryBlockingClient(channel);
            client.checkHealth("Before call");
            client.greet(users[0]);
            client.checkHealth("After user " + users[0]);

            for (String user : users) {
                client.greet(user);
                Thread.sleep(100);
            }

            client.checkHealth("After all users");
            Thread.sleep(10000);
            client.checkHealth("After 10 second wait");

            client.greet("Larry");
        } finally {
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
