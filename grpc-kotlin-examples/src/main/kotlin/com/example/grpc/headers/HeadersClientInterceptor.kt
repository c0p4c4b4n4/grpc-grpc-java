package com.example.grpc.headers

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import java.util.logging.Logger

internal class HeadersClientInterceptor : ClientInterceptor {
  private val logger: Logger = Logger.getLogger(HeadersClientInterceptor::class.java.getName())

  private val CUSTOM_HEADER_KEY: Metadata.Key<String?> =
    Metadata.Key.of<String?>("custom_client_header", Metadata.ASCII_STRING_MARSHALLER)

  override fun <ReqT, RespT> interceptCall(
    method: MethodDescriptor<ReqT?, RespT?>?,
    callOptions: CallOptions?,
    next: Channel
  ): ClientCall<ReqT?, RespT?> {
    return object : SimpleForwardingClientCall<ReqT?, RespT?>(next.newCall<ReqT?, RespT?>(method, callOptions)) {
      override fun start(responseListener: Listener<RespT?>?, headers: Metadata) {
        headers.put<String?>(CUSTOM_HEADER_KEY, "custom_request_value")
        super.start(object : SimpleForwardingClientCallListener<RespT?>(responseListener) {
          override fun onHeaders(responseHeaders: Metadata?) {
            logger.info("header received from server: $responseHeaders")
            super.onHeaders(responseHeaders)
          }
        }, headers)
      }
    }
  }
}
