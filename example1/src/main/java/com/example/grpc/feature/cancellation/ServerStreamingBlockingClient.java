package com.example.grpc.feature.cancellation;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Loggers;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerStreamingBlockingClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingBlockingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();

            Context.CancellableContext cancellableContext = Context.current().withCancellation();

            try {
                cancellableContext.run(() -> {
                    var responses = blockingStub.serverStreamingEcho(request);

                    int i = 0;
                    while (responses.hasNext()) {
                        logger.info("response: " + responses.next().getMessage());

                        if (++i > 3) {
                            cancellableContext.cancel(new Exception("Client cancelled the server streaming"));
                        }
                    }
                });
            } finally {
                cancellableContext.cancel(null);
            }

        } catch (StatusRuntimeException e) {
            logger.warning("error: " + e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
