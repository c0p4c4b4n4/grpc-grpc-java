package io.grpc.examples.echo2.advanced;

import io.grpc.*;

public class LoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {
        System.out.println("method called: " + call.getMethodDescriptor().getFullMethodName());
        return next.startCall(call, headers);
    }
}
