package com.example.grpc.echo.streaming.bidirectional;

import com.example.grpc.Servers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BidirectionalStreamingServer {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(EchoRequest request) {
                    logger.log(Level.INFO, "next: {0}", request.getMessage());
                    var response1 = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                    responseObserver.onNext(response1);
                    var response2 = EchoResponse.newBuilder().setMessage("hallo " + request.getMessage()).build();
                    responseObserver.onNext(response2);
                    var response3 = EchoResponse.newBuilder().setMessage("bonjour " + request.getMessage()).build();
                    responseObserver.onNext(response3);
                }

                @Override
                public void onError(Throwable t) {
                    logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    logger.info("completed");
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
