package com.example.grpc.features.retrying;

import com.example.grpc.Servers;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RetryingUnaryServer {

    private static final Logger logger = Logger.getLogger(RetryingUnaryServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        private static final float UNAVAILABLE_PERCENTAGE = 0.5F;
        private final Random random = new Random();
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            int count = counter.incrementAndGet();
            if (random.nextFloat() < UNAVAILABLE_PERCENTAGE) {
                logger.info("returning UNAVAILABLE error, count: " + count);
                responseObserver.onError(Status.UNAVAILABLE.withDescription("Server temporarily unavailable...").asRuntimeException());
            } else {
                logger.info("returning successful response, count: " + count);
                EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }
    }
}
