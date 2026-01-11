package com.example.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class EchoServiceGrpc {

  private EchoServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "example.grpc.EchoService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getUnaryEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UnaryEcho",
      requestType = com.example.grpc.EchoRequest.class,
      responseType = com.example.grpc.EchoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getUnaryEchoMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse> getUnaryEchoMethod;
    if ((getUnaryEchoMethod = EchoServiceGrpc.getUnaryEchoMethod) == null) {
      synchronized (EchoServiceGrpc.class) {
        if ((getUnaryEchoMethod = EchoServiceGrpc.getUnaryEchoMethod) == null) {
          EchoServiceGrpc.getUnaryEchoMethod = getUnaryEchoMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UnaryEcho"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EchoServiceMethodDescriptorSupplier("UnaryEcho"))
              .build();
        }
      }
    }
    return getUnaryEchoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getServerStreamingEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ServerStreamingEcho",
      requestType = com.example.grpc.EchoRequest.class,
      responseType = com.example.grpc.EchoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getServerStreamingEchoMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse> getServerStreamingEchoMethod;
    if ((getServerStreamingEchoMethod = EchoServiceGrpc.getServerStreamingEchoMethod) == null) {
      synchronized (EchoServiceGrpc.class) {
        if ((getServerStreamingEchoMethod = EchoServiceGrpc.getServerStreamingEchoMethod) == null) {
          EchoServiceGrpc.getServerStreamingEchoMethod = getServerStreamingEchoMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ServerStreamingEcho"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EchoServiceMethodDescriptorSupplier("ServerStreamingEcho"))
              .build();
        }
      }
    }
    return getServerStreamingEchoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getClientStreamingEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ClientStreamingEcho",
      requestType = com.example.grpc.EchoRequest.class,
      responseType = com.example.grpc.EchoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getClientStreamingEchoMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse> getClientStreamingEchoMethod;
    if ((getClientStreamingEchoMethod = EchoServiceGrpc.getClientStreamingEchoMethod) == null) {
      synchronized (EchoServiceGrpc.class) {
        if ((getClientStreamingEchoMethod = EchoServiceGrpc.getClientStreamingEchoMethod) == null) {
          EchoServiceGrpc.getClientStreamingEchoMethod = getClientStreamingEchoMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientStreamingEcho"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EchoServiceMethodDescriptorSupplier("ClientStreamingEcho"))
              .build();
        }
      }
    }
    return getClientStreamingEchoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getBidirectionalStreamingEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BidirectionalStreamingEcho",
      requestType = com.example.grpc.EchoRequest.class,
      responseType = com.example.grpc.EchoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.example.grpc.EchoRequest,
      com.example.grpc.EchoResponse> getBidirectionalStreamingEchoMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse> getBidirectionalStreamingEchoMethod;
    if ((getBidirectionalStreamingEchoMethod = EchoServiceGrpc.getBidirectionalStreamingEchoMethod) == null) {
      synchronized (EchoServiceGrpc.class) {
        if ((getBidirectionalStreamingEchoMethod = EchoServiceGrpc.getBidirectionalStreamingEchoMethod) == null) {
          EchoServiceGrpc.getBidirectionalStreamingEchoMethod = getBidirectionalStreamingEchoMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BidirectionalStreamingEcho"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.EchoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EchoServiceMethodDescriptorSupplier("BidirectionalStreamingEcho"))
              .build();
        }
      }
    }
    return getBidirectionalStreamingEchoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EchoServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EchoServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EchoServiceStub>() {
        @java.lang.Override
        public EchoServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EchoServiceStub(channel, callOptions);
        }
      };
    return EchoServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static EchoServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EchoServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EchoServiceBlockingV2Stub>() {
        @java.lang.Override
        public EchoServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EchoServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return EchoServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EchoServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EchoServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EchoServiceBlockingStub>() {
        @java.lang.Override
        public EchoServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EchoServiceBlockingStub(channel, callOptions);
        }
      };
    return EchoServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EchoServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EchoServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EchoServiceFutureStub>() {
        @java.lang.Override
        public EchoServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EchoServiceFutureStub(channel, callOptions);
        }
      };
    return EchoServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void unaryEcho(com.example.grpc.EchoRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUnaryEchoMethod(), responseObserver);
    }

    /**
     */
    default void serverStreamingEcho(com.example.grpc.EchoRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getServerStreamingEchoMethod(), responseObserver);
    }

    /**
     */
    default io.grpc.stub.StreamObserver<com.example.grpc.EchoRequest> clientStreamingEcho(
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getClientStreamingEchoMethod(), responseObserver);
    }

    /**
     */
    default io.grpc.stub.StreamObserver<com.example.grpc.EchoRequest> bidirectionalStreamingEcho(
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getBidirectionalStreamingEchoMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service EchoService.
   */
  public static abstract class EchoServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return EchoServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service EchoService.
   */
  public static final class EchoServiceStub
      extends io.grpc.stub.AbstractAsyncStub<EchoServiceStub> {
    private EchoServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EchoServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EchoServiceStub(channel, callOptions);
    }

    /**
     */
    public void unaryEcho(com.example.grpc.EchoRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUnaryEchoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void serverStreamingEcho(com.example.grpc.EchoRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getServerStreamingEchoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.example.grpc.EchoRequest> clientStreamingEcho(
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getClientStreamingEchoMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.example.grpc.EchoRequest> bidirectionalStreamingEcho(
        io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getBidirectionalStreamingEchoMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service EchoService.
   */
  public static final class EchoServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<EchoServiceBlockingV2Stub> {
    private EchoServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EchoServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EchoServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.example.grpc.EchoResponse unaryEcho(com.example.grpc.EchoRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getUnaryEchoMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, com.example.grpc.EchoResponse>
        serverStreamingEcho(com.example.grpc.EchoRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getServerStreamingEchoMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>
        clientStreamingEcho() {
      return io.grpc.stub.ClientCalls.blockingClientStreamingCall(
          getChannel(), getClientStreamingEchoMethod(), getCallOptions());
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<com.example.grpc.EchoRequest, com.example.grpc.EchoResponse>
        bidirectionalStreamingEcho() {
      return io.grpc.stub.ClientCalls.blockingBidiStreamingCall(
          getChannel(), getBidirectionalStreamingEchoMethod(), getCallOptions());
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service EchoService.
   */
  public static final class EchoServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<EchoServiceBlockingStub> {
    private EchoServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EchoServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EchoServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.grpc.EchoResponse unaryEcho(com.example.grpc.EchoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUnaryEchoMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.example.grpc.EchoResponse> serverStreamingEcho(
        com.example.grpc.EchoRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getServerStreamingEchoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service EchoService.
   */
  public static final class EchoServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<EchoServiceFutureStub> {
    private EchoServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EchoServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EchoServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.EchoResponse> unaryEcho(
        com.example.grpc.EchoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUnaryEchoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_UNARY_ECHO = 0;
  private static final int METHODID_SERVER_STREAMING_ECHO = 1;
  private static final int METHODID_CLIENT_STREAMING_ECHO = 2;
  private static final int METHODID_BIDIRECTIONAL_STREAMING_ECHO = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UNARY_ECHO:
          serviceImpl.unaryEcho((com.example.grpc.EchoRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse>) responseObserver);
          break;
        case METHODID_SERVER_STREAMING_ECHO:
          serviceImpl.serverStreamingEcho((com.example.grpc.EchoRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CLIENT_STREAMING_ECHO:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.clientStreamingEcho(
              (io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse>) responseObserver);
        case METHODID_BIDIRECTIONAL_STREAMING_ECHO:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.bidirectionalStreamingEcho(
              (io.grpc.stub.StreamObserver<com.example.grpc.EchoResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getUnaryEchoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.EchoRequest,
              com.example.grpc.EchoResponse>(
                service, METHODID_UNARY_ECHO)))
        .addMethod(
          getServerStreamingEchoMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.example.grpc.EchoRequest,
              com.example.grpc.EchoResponse>(
                service, METHODID_SERVER_STREAMING_ECHO)))
        .addMethod(
          getClientStreamingEchoMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.example.grpc.EchoRequest,
              com.example.grpc.EchoResponse>(
                service, METHODID_CLIENT_STREAMING_ECHO)))
        .addMethod(
          getBidirectionalStreamingEchoMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              com.example.grpc.EchoRequest,
              com.example.grpc.EchoResponse>(
                service, METHODID_BIDIRECTIONAL_STREAMING_ECHO)))
        .build();
  }

  private static abstract class EchoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EchoServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.grpc.Echo.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EchoService");
    }
  }

  private static final class EchoServiceFileDescriptorSupplier
      extends EchoServiceBaseDescriptorSupplier {
    EchoServiceFileDescriptorSupplier() {}
  }

  private static final class EchoServiceMethodDescriptorSupplier
      extends EchoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    EchoServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (EchoServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EchoServiceFileDescriptorSupplier())
              .addMethod(getUnaryEchoMethod())
              .addMethod(getServerStreamingEchoMethod())
              .addMethod(getClientStreamingEchoMethod())
              .addMethod(getBidirectionalStreamingEchoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
