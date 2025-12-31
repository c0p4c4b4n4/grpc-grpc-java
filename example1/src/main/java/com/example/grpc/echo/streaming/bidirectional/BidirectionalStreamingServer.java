package com.example.grpc.echo.streaming.bidirectional;

import com.example.grpc.Servers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

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
                    logger.info("next: " + request.getMessage());
                    var response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    logger.warning("error: " + Status.fromThrowable(t));
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
