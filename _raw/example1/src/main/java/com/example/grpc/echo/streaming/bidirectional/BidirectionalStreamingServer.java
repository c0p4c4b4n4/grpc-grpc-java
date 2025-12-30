package com.example.grpc.echo.streaming.bidirectional;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import com.example.grpc.echo.Shutdown;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BidirectionalStreamingServer {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Logging.init();

        int port = 50051;
        Server server = ServerBuilder.forPort(port)
            .addService(new EchoServiceImpl())
            .build()
            .start();

        logger.log(Level.INFO, "server started, listening on {0}", port);

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<EchoRequest>() {
                @Override
                public void onNext(EchoRequest request) {
                    System.out.println("server next: " + request.getMessage());
                    EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    logger.warning("error: " + Status.fromThrowable(t));
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
