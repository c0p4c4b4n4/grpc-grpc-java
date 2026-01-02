package com.example.grpc.manualflowcontrol;


import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ManualFlowControlClient {

    private static final Logger logger = Logger.getLogger(ManualFlowControlClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(1);

        ManagedChannel channel = ManagedChannelBuilder            .forAddress("localhost", 50051)            .usePlaintext()            .build();
        EchoServiceGrpc.EchoServiceStub stub = EchoServiceGrpc.newStub(channel);

        // When using manual flow-control and back-pressure on the client, the ClientResponseObserver handles both
        // request and response streams.
        ClientResponseObserver<EchoRequest, EchoResponse> clientResponseObserver =
            new ClientResponseObserver<EchoRequest, EchoResponse>() {

                ClientCallStreamObserver<EchoRequest> requestStream;

                @Override
                public void beforeStart(final ClientCallStreamObserver<EchoRequest> requestStream) {
                    this.requestStream = requestStream;
                    // Set up manual flow control for the response stream. It feels backwards to configure the response
                    // stream's flow control using the request stream's observer, but this is the way it is.
                    requestStream.disableAutoRequestWithInitial(1);

                    // Set up a back-pressure-aware producer for the request stream. The onReadyHandler will be invoked
                    // when the consuming side has enough buffer space to receive more messages.
                    //
                    // Messages are serialized into a transport-specific transmit buffer. Depending on the size of this buffer,
                    // MANY messages may be buffered, however, they haven't yet been sent to the server. The server must call
                    // request() to pull a buffered message from the client.
                    //
                    // Note: the onReadyHandler's invocation is serialized on the same thread pool as the incoming
                    // StreamObserver's onNext(), onError(), and onComplete() handlers. Blocking the onReadyHandler will prevent
                    // additional messages from being processed by the incoming StreamObserver. The onReadyHandler must return
                    // in a timely manner or else message processing throughput will suffer.
                    requestStream.setOnReadyHandler(new Runnable() {
                        // An iterator is used so we can pause and resume iteration of the request data.
                        Iterator<String> iterator = names().iterator();

                        @Override
                        public void run() {
                            // Start generating values from where we left off on a non-gRPC thread.
                            while (requestStream.isReady()) {
                                if (iterator.hasNext()) {
                                    // Send more messages if there are more messages to send.
                                    String name = iterator.next();
                                    logger.info("--> " + name);
                                    EchoRequest request = EchoRequest.newBuilder().setMessage(name).build();
                                    requestStream.onNext(request);
                                } else {
                                    // Signal completion if there is nothing left to send.
                                    requestStream.onCompleted();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onNext(EchoResponse value) {
                    logger.info("<-- " + value.getMessage());
                    // Signal the sender to send one message.
                    requestStream.request(1);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    done.countDown();
                }

                @Override
                public void onCompleted() {
                    logger.info("All Done");
                    done.countDown();
                }
            };

        // Note: clientResponseObserver is handling both request and response stream processing.
        stub.bidirectionalStreamingEcho(clientResponseObserver);

        done.await();

        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }

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
