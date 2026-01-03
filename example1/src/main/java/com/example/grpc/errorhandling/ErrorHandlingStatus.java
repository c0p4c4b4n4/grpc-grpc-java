package com.example.grpc.errorhandling;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ErrorHandlingStatus {

    private ManagedChannel channel;

    public static void main(String[] args) throws Exception {
        new ErrorHandlingStatus().run();
    }

    void run() throws Exception {
        var server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
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
        var status = Status.fromThrowable(t);
        Verify.verify(status.getCode() == Status.Code.INTERNAL);
        Verify.verify(status.getDescription().equals("Eggplant"));
    }

    void blockingCall() {
        var stub = EchoServiceGrpc.newBlockingStub(channel);
        try {
            stub.unaryEcho(EchoRequest.newBuilder().setMessage("Bart").build());
        } catch (Exception e) {
            verifyErrorResponse(e);
        }
    }

    void futureCallDirect() {
        var stub = EchoServiceGrpc.newFutureStub(channel);
        var response = stub.unaryEcho(EchoRequest.newBuilder().setMessage("Lisa").build());

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
        var stub = EchoServiceGrpc.newFutureStub(channel);
        var response = stub.unaryEcho(EchoRequest.newBuilder().setMessage("Maggie").build());

        var done = new CountDownLatch(1);
        Futures.addCallback(response, new FutureCallback<>() {
                @Override
                public void onSuccess(EchoResponse result) {
                    // won't be called
                }

                @Override
                public void onFailure(Throwable t) {
                    verifyErrorResponse(t);
                    done.countDown();
                }
            },
            MoreExecutors.directExecutor());

        if (!Uninterruptibles.awaitUninterruptibly(done, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }

    void asyncCall() {
        var stub = EchoServiceGrpc.newStub(channel);
        var request = EchoRequest.newBuilder().setMessage("Homer").build();

        var done = new CountDownLatch(1);
        var responseObserver = new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                // won't be called
            }

            @Override
            public void onError(Throwable t) {
                verifyErrorResponse(t);
                done.countDown();
            }

            @Override
            public void onCompleted() {
                // won't be called
            }
        };
        stub.unaryEcho(request, responseObserver);

        if (!Uninterruptibles.awaitUninterruptibly(done, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }
}

