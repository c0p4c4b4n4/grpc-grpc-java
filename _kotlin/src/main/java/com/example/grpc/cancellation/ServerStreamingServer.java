package com.example.grpc.cancellation;

import com.example.grpc.Delays;
import com.example.grpc.Servers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ ServerStreamingServer {

    private static final Logger logger = Logger.getLogger(ServerStreamingServer.class.getName());

    public static void main(String[] args) throws Exception {
        Servers.start(new EchoServiceImpl(), logger);
    }

    private static class /*TODO*/ EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            logger.log(Level.INFO, "request: {0}", request.getMessage());

            var serverObserver = (ServerCallStreamObserver<EchoResponse>) responseObserver;
            for (var i = 1; i <= 7; i++) {
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
