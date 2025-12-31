package com.example.grpc.features.healthservice;

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
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.health.v1.HealthGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnaryBlockingClient extends Loggable {

    private static final Logger logger = Logger.getLogger(UnaryBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Loggers.init();

        String target = "localhost:50051";
        String[] users = {"Alpha", "Beta", "Gamma"};

        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

        try {
            EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub;

            HealthGrpc.HealthStub healthStub= HealthGrpc.newStub(channel);
            HealthGrpc.HealthBlockingStub healthBlockingStub;

            echoBlockingStub = EchoServiceGrpc.newBlockingStub(channel);
            healthBlockingStub = HealthGrpc.newBlockingStub(channel);

//            UnaryBlockingClient client = new UnaryBlockingClient(channel);
            checkHealth(healthBlockingStub, "Before call");
            greet(echoBlockingStub, users[0]);
            checkHealth(healthBlockingStub, "After user " + users[0]);

            for (String user : users) {
                greet(echoBlockingStub, user);
                Thread.sleep(100);
            }

            checkHealth(healthBlockingStub, "After all users");
            Thread.sleep(10000);
            checkHealth(healthBlockingStub, "After 10 second wait");

            greet(echoBlockingStub, "Delta");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }


    private static ServingStatus checkHealth(HealthGrpc.HealthBlockingStub healthBlockingStub, String prefix) {
        HealthCheckRequest healthRequest = HealthCheckRequest.getDefaultInstance();
        HealthCheckResponse response = healthBlockingStub.check(healthRequest);
        logger.info(prefix + ", current health is: " + response.getStatus());
        return response.getStatus();
    }

    public static void greet(EchoServiceGrpc.EchoServiceBlockingStub echoBlockingStub, String name) {
        logger.info("Will try to greet " + name + " ...");
        EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
        EchoResponse response;
        try {
            response = echoBlockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

}
