package com.example.grpc.features.errorhandling;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ErrorHandlingStatus {

    public static void main(String[] args) throws Exception {
        new ErrorHandlingStatus().run();
    }

    private ManagedChannel channel;

    void run() throws Exception {
        Server server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
            .addService(new EchoServiceGrpc.EchoServiceImplBase() {
                @Override
                public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
                    responseObserver.onError(Status.INTERNAL.withDescription("Eggplant").asRuntimeException());
                }
            })
            .build()
            .start();

        channel = Grpc.newChannelBuilderForAddress("localhost", server.getPort(), InsecureChannelCredentials.create()).build();

        blockingCall();
        futureCallDirect();
        futureCallCallback();
        asyncCall();

        channel.shutdown();
        server.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
        server.awaitTermination();
    }

    private void verifyErrorResponse(Throwable t) {
        Status status = Status.fromThrowable(t);
        Verify.verify(status.getCode() == Status.Code.INTERNAL);
        Verify.verify(status.getDescription().equals("Eggplant"));
    }

    void blockingCall() {
        EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);
        try {
            stub.unaryEcho(EchoRequest.newBuilder().setMessage("Bart").build());
        } catch (Exception e) {
            verifyErrorResponse(e);
        }
    }

    void futureCallDirect() {
        EchoServiceGrpc.EchoServiceFutureStub stub = EchoServiceGrpc.newFutureStub(channel);
        ListenableFuture<EchoResponse> response = stub.unaryEcho(EchoRequest.newBuilder().setMessage("Lisa").build());

        try {
            response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            verifyErrorResponse(e.getCause());
        }
    }

    void futureCallCallback() {
        EchoServiceGrpc.EchoServiceFutureStub stub = EchoServiceGrpc.newFutureStub(channel);
        ListenableFuture<EchoResponse> response = stub.unaryEcho(EchoRequest.newBuilder().setMessage("Maggie").build());

        CountDownLatch latch = new CountDownLatch(1);
        Futures.addCallback(response, new FutureCallback<EchoResponse>() {
                @Override
                public void onSuccess(EchoResponse result) {
                    // won't be called
                }

                @Override
                public void onFailure(Throwable t) {
                    verifyErrorResponse(t);
                    latch.countDown();
                }
            },
            MoreExecutors.directExecutor());

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }

    void asyncCall() {
        EchoServiceGrpc.EchoServiceStub stub = EchoServiceGrpc.newStub(channel);
        EchoRequest request = EchoRequest.newBuilder().setMessage("Homer").build();

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<EchoResponse> responseObserver = new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                // won't be called
            }

            @Override
            public void onError(Throwable t) {
                verifyErrorResponse(t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                // won't be called
            }
        };
        stub.unaryEcho(request, responseObserver);

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }
}

