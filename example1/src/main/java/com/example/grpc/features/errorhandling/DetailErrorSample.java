package com.example.grpc.features.errorhandling;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.rpc.DebugInfo;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/**
 * Shows how to setting and reading RPC error details.
 * Proto used here is just an example proto, but the pattern sending
 * application error information as an application-specific binary protos
 * in the response trailers is the recommended way to return application
 * level error.
 */
public class DetailErrorSample {
  private static final Metadata.Key<DebugInfo> DEBUG_INFO_TRAILER_KEY =
      ProtoUtils.keyForProto(DebugInfo.getDefaultInstance());

  private static final DebugInfo DEBUG_INFO =
      DebugInfo.newBuilder()
          .addStackEntries("stack_entry_1")
          .addStackEntries("stack_entry_2")
          .addStackEntries("stack_entry_3")
          .setDetail("detailed error info.").build();

  private static final String DEBUG_DESC = "detailed error description";

  public static void main(String[] args) throws Exception {
    new DetailErrorSample().run();
  }

  private ManagedChannel channel;

  void run() throws Exception {
    Server server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
        .addService(new EchoServiceGrpc.GreeterImplBase() {
      @Override
      public void sayHello(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        Metadata trailers = new Metadata();
        trailers.put(DEBUG_INFO_TRAILER_KEY, DEBUG_INFO);
        responseObserver.onError(Status.INTERNAL.withDescription(DEBUG_DESC)
            .asRuntimeException(trailers));
      }
    }).build().start();
    channel = Grpc.newChannelBuilderForAddress(
          "localhost", server.getPort(), InsecureChannelCredentials.create()).build();

    blockingCall();
    futureCallDirect();
    futureCallCallback();
    asyncCall();
    advancedAsyncCall();

    channel.shutdown();
    server.shutdown();
    channel.awaitTermination(1, TimeUnit.SECONDS);
    server.awaitTermination();
  }

  static void verifyErrorReply(Throwable t) {
    Status status = Status.fromThrowable(t);
    Metadata trailers = Status.trailersFromThrowable(t);
    Verify.verify(status.getCode() == Status.Code.INTERNAL);
    Verify.verify(trailers.containsKey(DEBUG_INFO_TRAILER_KEY));
    Verify.verify(status.getDescription().equals(DEBUG_DESC));
    try {
      Verify.verify(trailers.get(DEBUG_INFO_TRAILER_KEY).equals(DEBUG_INFO));
    } catch (IllegalArgumentException e) {
      throw new VerifyException(e);
    }
  }

  void blockingCall() {
    EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);
    try {
      stub.unaryEcho(EchoRequest.newBuilder().build());
    } catch (Exception e) {
      verifyErrorReply(e);
    }
  }

  void futureCallDirect() {
    EchoServiceGrpc.EchoServiceFutureStub stub = EchoServiceGrpc.newFutureStub(channel);
    ListenableFuture<EchoResponse> response =
        stub.unaryEcho(EchoRequest.newBuilder().build());

    try {
      response.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      verifyErrorReply(e.getCause());
    }
  }

  void futureCallCallback() {
    GreeterFutureStub stub = EchoServiceGrpc.newFutureStub(channel);
    ListenableFuture<EchoResponse> response =
        stub.sayHello(EchoRequest.newBuilder().build());

    final CountDownLatch latch = new CountDownLatch(1);

    Futures.addCallback(
        response,
        new FutureCallback<EchoResponse>() {
          @Override
          public void onSuccess(@Nullable EchoResponse result) {
            // Won't be called, since the server in this example always fails.
          }

          @Override
          public void onFailure(Throwable t) {
            verifyErrorReply(t);
            latch.countDown();
          }
        },
        directExecutor());

    if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
      throw new RuntimeException("timeout!");
    }
  }

  void asyncCall() {
    GreeterStub stub = EchoServiceGrpc.newStub(channel);
    EchoRequest request = EchoRequest.newBuilder().build();
    final CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<EchoResponse> responseObserver = new StreamObserver<EchoResponse>() {

      @Override
      public void onNext(EchoResponse value) {
        // Won't be called.
      }

      @Override
      public void onError(Throwable t) {
        verifyErrorReply(t);
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always fails.
      }
    };
    stub.sayHello(request, responseObserver);

    if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
      throw new RuntimeException("timeout!");
    }
  }


  /**
   * This is more advanced and does not make use of the stub.  You should not normally need to do
   * this, but here is how you would.
   */
  void advancedAsyncCall() {
    ClientCall<EchoRequest, EchoResponse> call =
        channel.newCall(EchoServiceGrpc.getSayHelloMethod(), CallOptions.DEFAULT);

    final CountDownLatch latch = new CountDownLatch(1);

    call.start(new ClientCall.Listener<EchoResponse>() {

      @Override
      public void onClose(Status status, Metadata trailers) {
        Verify.verify(status.getCode() == Status.Code.INTERNAL);
        Verify.verify(trailers.containsKey(DEBUG_INFO_TRAILER_KEY));
        try {
          Verify.verify(trailers.get(DEBUG_INFO_TRAILER_KEY).equals(DEBUG_INFO));
        } catch (IllegalArgumentException e) {
          throw new VerifyException(e);
        }

        latch.countDown();
      }
    }, new Metadata());

    call.sendMessage(EchoRequest.newBuilder().build());
    call.halfClose();

    if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
      throw new RuntimeException("timeout!");
    }
  }
}

