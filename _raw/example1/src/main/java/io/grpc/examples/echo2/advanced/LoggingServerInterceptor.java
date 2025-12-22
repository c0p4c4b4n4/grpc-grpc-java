package io.grpc.examples.echo2.advanced;

import io.grpc.*;

public class LoggingServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {
        System.out.println("call server method: " + call.getMethodDescriptor().getFullMethodName());
        return next.startCall(call, headers);
    }
}
