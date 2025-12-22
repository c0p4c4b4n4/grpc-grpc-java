package io.grpc.examples.echo2.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.echo2.Logging;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoBlockingClient.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        EchoResponse response = blockingStub.unaryEcho(request);
        logger.info("response received: " + response.getMessage());

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
