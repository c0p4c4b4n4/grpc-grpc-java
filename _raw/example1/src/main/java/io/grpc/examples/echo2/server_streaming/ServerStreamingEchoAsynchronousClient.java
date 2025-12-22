package io.grpc.examples.echo2.server_streaming;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.echo2.Logging;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStreamingEchoAsynchronousClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingEchoAsynchronousClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        asyncStub.serverStreamingEcho(
            request,
            new StreamObserver<EchoResponse>() {
                @Override
                public void onNext(EchoResponse value) {
//                    System.out.println("Received an echo: " + response.getMessage());
                    System.out.println("async received: " + value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    logger.warning("error: " + Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
//                    System.out.println("Server acknowledged end of echo stream.");
                    System.out.println("completed");
                }
            });

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
