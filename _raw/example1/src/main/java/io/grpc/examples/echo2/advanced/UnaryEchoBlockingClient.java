package io.grpc.examples.echo2.advanced;

import io.grpc.Deadline;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnaryEchoBlockingClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoBlockingClient.class.getName());

    public void greet(EchoServiceGrpc.EchoServiceBlockingStub blockingStub, String name) {
        logger.info("Will try to greet " + name + " ...");
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        EchoResponse response;
        try {
            response = blockingStub.unaryEcho(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        try {
            EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new LoggingClientInterceptor())
                .withWaitForReady().withDeadline(Deadline.after(20, TimeUnit.SECONDS));
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            EchoResponse response = blockingStub.unaryEcho(request);
            logger.info("result: " + response.getMessage());
        } finally {
//            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
