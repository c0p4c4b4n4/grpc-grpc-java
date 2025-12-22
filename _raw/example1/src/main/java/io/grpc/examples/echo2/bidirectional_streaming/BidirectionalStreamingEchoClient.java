package io.grpc.examples.echo2.bidirectional_streaming;

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

public class BidirectionalStreamingEchoClient {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingEchoClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

        EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
        StreamObserver<EchoRequest> requestObserver = asyncStub.bidirectionalStreamingEcho(new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                System.out.println("client next: " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                System.out.println("client completed: stream closed by server");
            }
        });

        requestObserver.onNext(EchoRequest.newBuilder().setMessage("one").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("two").build());
        requestObserver.onNext(EchoRequest.newBuilder().setMessage("three").build());
        requestObserver.onCompleted();

        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }
}
