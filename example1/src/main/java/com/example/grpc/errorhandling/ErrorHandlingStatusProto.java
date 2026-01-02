package com.example.grpc.errorhandling;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Code;
import com.google.rpc.DebugInfo;
import com.google.rpc.Status;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ErrorHandlingStatusProto {

    private static final DebugInfo DEBUG_INFO =
        DebugInfo.newBuilder()
            .addStackEntries("stack_entry_1")
            .addStackEntries("stack_entry_2")
            .addStackEntries("stack_entry_3")
            .setDetail("detailed error info.").build();

    public static void main(String[] args) throws Exception {
        Server server = null;
        ManagedChannel channel = null;

        try {
            server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
                .addService(new EchoServiceGrpc.EchoServiceImplBase() {
                    @Override
                    public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
                        var status = Status.newBuilder()
                            .setCode(Code.INVALID_ARGUMENT.getNumber())
                            .setMessage("Email or password malformed")
                            .addDetails(Any.pack(DEBUG_INFO))
                            .build();
                        responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                    }
                })
                .build()
                .start();

            channel = Grpc.newChannelBuilderForAddress("localhost", server.getPort(), InsecureChannelCredentials.create()).build();

            blockingCall(channel);
            futureCallDirect(channel);
            futureCallCallback(channel);
            asyncCall(channel);
        } finally {
            cleanup(channel, server);
        }
    }

    private static void cleanup(ManagedChannel channel, Server server) throws InterruptedException {
        if (channel != null) {
            channel.shutdown();
        }
        if (server != null) {
            server.shutdown();
        }

        if (channel != null) {
            channel.awaitTermination(1, TimeUnit.SECONDS);
        }
        if (server != null) {
            server.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    static void verifyErrorResponse(Throwable t) {
        var status = StatusProto.fromThrowable(t);
        Verify.verify(status.getCode() == Code.INVALID_ARGUMENT.getNumber());
        Verify.verify(status.getMessage().equals("Email or password malformed"));
        try {
            var unpackedDetail = status.getDetails(0).unpack(DebugInfo.class);
            Verify.verify(unpackedDetail.equals(DEBUG_INFO));
        } catch (InvalidProtocolBufferException e) {
            Verify.verify(false, "Message was a different type than expected");
        }
    }

    static void blockingCall(Channel channel) {
        var stub = EchoServiceGrpc.newBlockingStub(channel);
        try {
            stub.unaryEcho(EchoRequest.newBuilder().build());
        } catch (Exception e) {
            verifyErrorResponse(e);
            System.out.println("Blocking call received expected error details");
        }
    }

    static void futureCallDirect(Channel channel) {
        var stub = EchoServiceGrpc.newFutureStub(channel);
        var response = stub.unaryEcho(EchoRequest.newBuilder().build());

        try {
            response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            verifyErrorResponse(e.getCause());
            System.out.println("Future call direct received expected error details");
        }
    }

    static void futureCallCallback(Channel channel) {
        var stub = EchoServiceGrpc.newFutureStub(channel);
        var response = stub.unaryEcho(EchoRequest.newBuilder().build());

        var done = new CountDownLatch(1);
        Futures.addCallback(response, new FutureCallback<EchoResponse>() {
                @Override
                public void onSuccess(EchoResponse result) {
                    // won't be called
                }

                @Override
                public void onFailure(Throwable t) {
                    verifyErrorResponse(t);
                    System.out.println("Future callback received expected error details");
                    done.countDown();
                }
            },
            MoreExecutors.directExecutor());

        if (!Uninterruptibles.awaitUninterruptibly(done, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }

    static void asyncCall(Channel channel) {
        var stub = EchoServiceGrpc.newStub(channel);
        var request = EchoRequest.newBuilder().build();

        var done = new CountDownLatch(1);
        var responseObserver = new StreamObserver<EchoResponse>() {

            @Override
            public void onNext(EchoResponse value) {
                // won't be called
            }

            @Override
            public void onError(Throwable t) {
                verifyErrorResponse(t);
                System.out.println("Async call received expected error details");
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

