package com.example.grpc.manualflowcontrol;

import com.example.grpc.Delays;
import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ ManualFlowControlServer {

    private static final Logger logger = Logger.getLogger(ManualFlowControlServer.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {
        Loggers.init();

        var port = 50051;
        var server = ServerBuilder
            .forPort(port)
            .addService(new EchoServiceImpl())
            .build()
            .start();

        logger.log(Level.INFO, "server started, listening on {0,number,#}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("server is shutting down");
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("server shutdown was interrupted");
                server.shutdownNow();
            }
            System.err.println("server has been shut down");
        }));

        server.awaitTermination();
    }

    private static class /*TODO*/ EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        @Override
        public StreamObserver<EchoRequest> bidirectionalStreamingEcho(final StreamObserver<EchoResponse> responseObserver) {
            var serverCallStreamObserver = (ServerCallStreamObserver<EchoResponse>) responseObserver;
            serverCallStreamObserver.disableAutoRequest();

            var onReadyHandler = new OnReadyHandler(serverCallStreamObserver);
            serverCallStreamObserver.setOnReadyHandler(onReadyHandler);

            return new StreamObserver<>() {
                int counter = 0;

                @Override
                public void onNext(EchoRequest request) {
                    try {
                        logger.log(Level.INFO, "request: {0}", request.getMessage());

                        Delays.sleep(++counter % 10 == 0 ? 5 : 0);

                        var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                        responseObserver.onNext(response);

                        if (serverCallStreamObserver.isReady()) {
                            serverCallStreamObserver.request(1);
                        } else {
                            onReadyHandler.wasReady.set(false);
                        }
                    } catch (Throwable t) {
                        logger.log(Level.SEVERE, "failure: {0}", t.getMessage());
                        responseObserver.onError(Status.UNKNOWN.withDescription("Error handling request").withCause(t).asException());
                    }
                }

                @Override
                public void onError(Throwable t) {
                    logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                    responseObserver.onCompleted();
                }

                @Override
                public void onCompleted() {
                    logger.info("completed");
                    responseObserver.onCompleted();
                }
            };
        }

        private static class /*TODO*/ OnReadyHandler implements Runnable {

            private final ServerCallStreamObserver<EchoResponse> serverCallStreamObserver;
            private final AtomicBoolean wasReady;

            OnReadyHandler(ServerCallStreamObserver<EchoResponse> serverCallStreamObserver) {
                this.serverCallStreamObserver = serverCallStreamObserver;
                this.wasReady = new AtomicBoolean(false);
            }

            @Override
            public void run() {
                if (serverCallStreamObserver.isReady() && !wasReady.get()) {
                    wasReady.set(true);

                    logger.info("ready");
                    serverCallStreamObserver.request(1);
                }
            }
        }
    }
}
