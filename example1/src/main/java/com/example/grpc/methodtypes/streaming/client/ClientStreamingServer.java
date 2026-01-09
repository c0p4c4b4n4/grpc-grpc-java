package com.example.grpc.methodtypes.streaming.client;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Servers;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientStreamingServer {

    private static final Logger logger = Logger.getLogger(ClientStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl());
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public StreamObserver<EchoRequest> clientStreamingEcho(StreamObserver<EchoResponse> responseObserver) {
            return new StreamObserver<>() {
                final StringBuilder accumulator = new StringBuilder();

                @Override
                public void onNext(EchoRequest request) {
                    logger.log(Level.INFO, "next request: {0}", request.getMessage());
                    accumulator.append(request.getMessage()).append(" ");
                }

                @Override
                public void onError(Throwable t) {
                    logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    var response = EchoResponse.newBuilder().setMessage(accumulator.toString().trim()).build();
                    logger.info("completed: " + response.getMessage());
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
