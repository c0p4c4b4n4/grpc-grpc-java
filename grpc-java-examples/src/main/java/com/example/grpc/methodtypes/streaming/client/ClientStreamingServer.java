package com.example.grpc.methodtypes.streaming.client;

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import com.example.grpc.Servers;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                final List<String> accumulator = Collections.synchronizedList(new ArrayList<>());

                @Override
                public void onNext(EchoRequest request) {
                    logger.log(Level.INFO, "next request: {0}", request.getMessage());
                    accumulator.add("hello " + request.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
                }

                @Override
                public void onCompleted() {
                    var response = EchoResponse.newBuilder().setMessage(String.join(", ", accumulator)).build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
