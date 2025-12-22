package io.grpc.examples.echo2.bidirectional_streaming;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.echo2.Logging;
import io.grpc.examples.echo2.Shutdown;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BidirectionalStreamingEchoServer {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        Server server = ServerBuilder.forPort(50051)
            .addService(new
                EchoServiceImpl()
            )
            .build()
            .start();

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<EchoRequest>() {
                @Override
                public void onNext(EchoRequest request) {
//                    logger.info("Received bidirection streaming echo request: " + request.getMessage());
                    System.out.println("server next: " + "server next: " + request.getMessage());
                    // Business logic: Echo back immediately or process
                    EchoResponse response = EchoResponse.newBuilder().setMessage("server next: " + request.getMessage()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
//                    logger.info("Bidirectional stream completed from client side");
                    System.out.println("server completed");
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
