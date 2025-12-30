package com.example.grpc.feature.cancellation;

import com.example.grpc.Delays;
import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.Servers;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ServerStreamingServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.info("request: " + request.getMessage());

            var serverObserver = (ServerCallStreamObserver<EchoResponse>) responseObserver;
            for (int i = 1; i <= 7; i++) {
                if (serverObserver.isCancelled()) {
                    logger.info("Server received cancellation");
                    responseObserver.onError(Status.CANCELLED.withDescription("Server confirmed cancellation").asRuntimeException());
                    return;
                }

                Delays.sleep(1);

                var message = "hello " + request.getMessage() + " " + i;
                var response = EchoResponse.newBuilder().setMessage(message).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
