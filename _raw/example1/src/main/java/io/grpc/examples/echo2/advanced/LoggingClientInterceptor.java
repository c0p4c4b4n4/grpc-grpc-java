package io.grpc.examples.echo2.advanced;

import io.grpc.*;

public class LoggingClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        Channel next) {
        System.out.println("call client method: " + method.getFullMethodName());
        return next.newCall(method, callOptions);
    }
}