package com.example.grpc.features.healthservice;

import com.example.grpc.Loggable;
import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.health.v1.HealthGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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

    public static void main(String[] args) throws Exception {
        Loggers.init();

        String target = "localhost:50051";
        String[] users = {"Alpha", "Beta", "Gamma"};

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

            client.greet("Delta");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }


    }
}
