package com.example.grpc.features.healthservice;

import com.example.grpc.Delays;
import com.example.grpc.Loggable;
import com.example.grpc.Loggers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnaryBlockingClient extends Loggable {

    private static final Logger logger = Logger.getLogger(UnaryBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        try {
            EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub = EchoServiceGrpc.newBlockingStub(channel);
            HealthGrpc.HealthBlockingStub healthBlockingStub = HealthGrpc.newBlockingStub(channel);

            String[] users = {"Alpha", "Beta", "Gamma"};

            checkHealth(healthBlockingStub, "before all users");
            greet(echoBlockingStub, users[0]);
            checkHealth(healthBlockingStub, "after user " + users[0]);

            for (String user : users) {
                greet(echoBlockingStub, user);
                Thread.sleep(100);
            }
            checkHealth(healthBlockingStub, "after all users");

            Delays.sleep(10);
            checkHealth(healthBlockingStub, "after 10 second wait");

            greet(echoBlockingStub, "Delta");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void checkHealth(HealthGrpc.HealthBlockingStub healthBlockingStub, String prefix) {
        var request = HealthCheckRequest.getDefaultInstance();
        var response = healthBlockingStub.check(request);
        logger.info(prefix + ", health is: " + response.getStatus());
    }

    public static void greet(EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub, String name) {
        logger.info("will try to request " + name + " ...");
        var request = EchoRequest.newBuilder().setMessage(name).build();
        EchoResponse response;
        try {
            response = echoBlockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("response: " + response.getMessage());
    }
}
