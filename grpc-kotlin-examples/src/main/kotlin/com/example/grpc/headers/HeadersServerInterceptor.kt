package com.example.grpc.headers

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import java.util.logging.Level
import java.util.logging.Logger

internal class HeadersServerInterceptor : ServerInterceptor {
  override fun <ReqT, RespT> interceptCall(
    call: ServerCall<ReqT?, RespT?>?,
    requestHeaders: Metadata?,
    next: ServerCallHandler<ReqT?, RespT?>
  ): ServerCall.Listener<ReqT?>? {
    logger.log(Level.INFO, "header received from client: {0}", requestHeaders)
    return next.startCall(object : SimpleForwardingServerCall<ReqT?, RespT?>(call) {
      override fun sendHeaders(responseHeaders: Metadata) {
        responseHeaders.put<String?>(CUSTOM_HEADER_KEY, "custom_response_value")
        super.sendHeaders(responseHeaders)
      }
    }, requestHeaders)
  }

  companion object {
    private val logger: Logger = Logger.getLogger(HeadersServerInterceptor::class.java.getName())
    private val CUSTOM_HEADER_KEY: Metadata.Key<String?> =
      Metadata.Key.of<String?>("custom_server_header", Metadata.ASCII_STRING_MARSHALLER)
  }
}
