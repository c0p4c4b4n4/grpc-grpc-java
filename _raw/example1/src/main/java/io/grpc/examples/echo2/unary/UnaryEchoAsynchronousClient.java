package io.grpc.examples.echo2.unary;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnaryEchoAsynchronousClient {

    private static final Logger logger = Logger.getLogger(UnaryEchoAsynchronousClient.class.getName());

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
        asyncStub.unaryEcho(request, new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse response) {
                logger.info("next: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("error: " + t);
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
            }
        });

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
