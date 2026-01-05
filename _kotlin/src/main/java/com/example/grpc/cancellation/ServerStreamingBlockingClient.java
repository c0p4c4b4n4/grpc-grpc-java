package com.example.grpc.cancellation;

import com.example.grpc.Loggers;
import com.example.grpc.EchoRequest;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Context;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class /*TODO*/ ServerStreamingBlockingClient {

    private static final Logger logger = Logger.getLogger(ServerStreamingBlockingClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Loggers.init();

        var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        try {
            var blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            var request = EchoRequest.newBuilder().setMessage("world").build();

            var cancellableContext = Context.current().withCancellation();

            try {
                cancellableContext.run(() -> {
                    var responses = blockingStub.serverStreamingEcho(request);

                    var i = 0;
                    while (responses.hasNext()) {
                        logger.info("response: " + responses.next().getMessage());

                        if (++i > 3) {
                            cancellableContext.cancel(new Exception("Client cancelled streaming"));
                        }
                    }
                });
            } finally {
                cancellableContext.cancel(null);
            }

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC error: {0}", e.getStatus());
        } finally {
            channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
