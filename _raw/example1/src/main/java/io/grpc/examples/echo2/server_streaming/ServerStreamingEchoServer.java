package io.grpc.examples.echo2.server_streaming;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.echo2.EchoRequest;
import io.grpc.examples.echo2.EchoResponse;
import io.grpc.examples.echo2.EchoServiceGrpc;
import io.grpc.examples.echo2.Logging;
import io.grpc.examples.echo2.Shutdown;
import io.grpc.stub.StreamObserver;

public class ServerStreamingEchoServer {

    public static void main(String[] args) throws Exception {
        Logging.init();

        Server server = ServerBuilder.forPort(50051)
            .addService(
                new EchoServiceImpl()
            )
            .build()
            .start();

        Shutdown.init(server);
        server.awaitTermination();
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
        @Override
        public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
//            logger.info("Received server streaming echo request: " + request.getMessage());
            for (int i = 1; i <= 5; i++) {
                String value = "echo [" + i + "]: " + request.getMessage();
                EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
