package com.example.grpc.echo.unary

import com.example.grpc.EchoRequest

object  /*TODO*/ UnaryServer {
    private val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(UnaryServer::class.java.getName())

    @kotlin.Throws(java.lang.Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: kotlin.Array<kotlin.String>) {
        Servers.start(EchoServiceImpl(), UnaryServer.logger)
    }

    private class  /*TODO*/ EchoServiceImpl : EchoServiceGrpc.EchoServiceImplBase() {
        public override fun unaryEcho(request: EchoRequest, responseObserver: StreamObserver<EchoResponse?>) {
            UnaryServer.logger.log(java.util.logging.Level.INFO, "request: {0}", request.getMessage())
            val response: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }
}
