package io.grpc.examples.echo2.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        EchoResponse response = blockingStub.unaryEcho(request);
        logger.info("response received: " + response.getMessage());

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
