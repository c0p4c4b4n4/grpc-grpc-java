package echo.unary

import com.example.grpc.echo.EchoRequest
import com.example.grpc.echo.EchoResponse
import com.example.grpc.echo.EchoServiceGrpc.EchoServiceImplBase
import echo.Logging
import echo.Shutdown
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import java.util.logging.Logger

object UnaryEchoServer {
    private val logger: Logger = Logger.getLogger(UnaryEchoServer::class.java.getName())

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Logging.init()

        val server = ServerBuilder.forPort(50051)
            .addService(
                object : EchoServiceImplBase() {
                    override fun unaryEcho(request: EchoRequest, responseObserver: StreamObserver<EchoResponse?>) {
                        logger.info("request: " + request.getMessage())
                        val response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build()
                        responseObserver.onNext(response)
                        responseObserver.onCompleted()
                    }
                }
            )
            .build()
            .start()

        logger.info("server started")

        Shutdown.init(server)
        server.awaitTermination()
    }
}
