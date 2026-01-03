package com.example.grpc.manualflowcontrol;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManualFlowControlClient {

    private static final Logger logger = Logger.getLogger(ManualFlowControlClient.class.getName());

    public static void main(String[] args) throws InterruptedException {

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        var stub = EchoServiceGrpc.newStub(channel);

        var done = new CountDownLatch(1);
        stub.bidirectionalStreamingEcho(new ClientResponseObserver<EchoRequest, EchoResponse>() {

            ClientCallStreamObserver<EchoRequest> requestStream;

            @Override
            public void beforeStart(ClientCallStreamObserver<EchoRequest> requestStream) {
                this.requestStream = requestStream;
                requestStream.disableAutoRequestWithInitial(1);

                Runnable onReadyHandler = new OnReadyHandler(requestStream);
                requestStream.setOnReadyHandler(onReadyHandler);
            }

            @Override
            public void onNext(EchoResponse value) {
                logger.info("<-- " + value.getMessage());
                // Signal the sender to send one message.
                requestStream.request(1);
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                done.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
                done.countDown();
            }
        }
        );

        done.await();

        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }

    private static class OnReadyHandler implements  Runnable {

        private final ClientCallStreamObserver<EchoRequest> requestStream;

        // An iterator is used so we can pause and resume iteration of the request data.
        Iterator<String> iterator = names().iterator();

        private OnReadyHandler(ClientCallStreamObserver<EchoRequest> requestStream) {
            this.requestStream = requestStream;
        }

        @Override
        public void run() {
            // Start generating values from where we left off on a non-gRPC thread.
            while (requestStream.isReady()) {
                if (iterator.hasNext()) {
                    // Send more messages if there are more messages to send.
                    var name = iterator.next();
                    logger.info("--> " + name);
                    var request = EchoRequest.newBuilder().setMessage(name).build();
                    requestStream.onNext(request);
                } else {
                    // Signal completion if there is nothing left to send.
                    requestStream.onCompleted();
                }
            }
        }
    };
    private static List<String> names() {
        return Arrays.asList(
            "Alpha",
            "Bravo",
            "Charlie",
            "Delta",
            "Echo",
            "Foxtrot",
            "Golf",
            "Hotel",
            "India",
            "Juliett",
            "Kilo",
            "Lima",
            "Mike",
            "November",
            "Oscar",
            "Papa",
            "Quebec",
            "Romeo",
            "Sierra",
            "Tango",
            "Uniform",
            "Victor",
            "Whiskey",
            "X-ray",
            "Yankee",
            "Zulu"
        );
    }
}
