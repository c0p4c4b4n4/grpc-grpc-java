package io.grpc.examples.echo2.client_streaming;

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

public class ClientStreamingEchoServer {

    private static final Logger logger = Logger.getLogger(ClientStreamingEchoServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        Server server = ServerBuilder.forPort(50051)
            .addService(
                new EchoServiceImpl()
            )
            .build()
            .start();

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public StreamObserver<EchoRequest> clientStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<EchoRequest>() {
                StringBuilder result = new StringBuilder();

                @Override
                public void onNext(EchoRequest request) {
//                    logger.info("Received client streaming echo request: " + request.getMessage());
                    String message = request.getMessage();
                    System.out.println("server next: " + message);
                    result.append(message).append(" ");
                }

                @Override
                public void onError(Throwable t) {
                    logger.warning("error: " + Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
//                    logger.info("Client streaming complete");
                    System.out.println("server completed: " + result.toString().trim());
                    responseObserver.onNext(EchoResponse.newBuilder()
                        .setMessage("server completed: " + result.toString().trim())
                        .build());
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
