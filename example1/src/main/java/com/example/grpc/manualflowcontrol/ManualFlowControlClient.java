package com.example.grpc.manualflowcontrol;

import com.example.grpc.Loggers;
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
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        var stub = EchoServiceGrpc.newStub(channel);

        var done = new CountDownLatch(1);
        stub.bidirectionalStreamingEcho(new ClientResponseObserver<EchoRequest, EchoResponse>() {

            private ClientCallStreamObserver<EchoRequest> requestStream;

            @Override
            public void beforeStart(ClientCallStreamObserver<EchoRequest> requestStream) {
                this.requestStream = requestStream;

                requestStream.disableAutoRequestWithInitial(1);
                requestStream.setOnReadyHandler(new OnReadyHandler(requestStream, getNames().iterator()));
            }

            @Override
            public void onNext(EchoResponse response) {
                logger.log(Level.INFO, "response: {0}", response.getMessage());
                requestStream.request(1);
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                done.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("responses completed");
                done.countDown();
            }
        });

        done.await();
        channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }

    private static List<String> getNames() {
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

    private static class OnReadyHandler implements Runnable {

        private final ClientCallStreamObserver<EchoRequest> requestStream;
        private final Iterator<String> iterator;

        private OnReadyHandler(ClientCallStreamObserver<EchoRequest> requestStream, Iterator<String> iterator) {
            this.requestStream = requestStream;
            this.iterator = iterator;
        }

        @Override
        public void run() {
            while (requestStream.isReady()) {
                if (iterator.hasNext()) {
                    var name = iterator.next();
                    logger.log(Level.INFO, "request: {0}", name);

                    var request = EchoRequest.newBuilder().setMessage(name).build();
                    requestStream.onNext(request);
                } else {
                    logger.info("requests completed");
                    requestStream.onCompleted();
                }
            }
        }
    }
}
