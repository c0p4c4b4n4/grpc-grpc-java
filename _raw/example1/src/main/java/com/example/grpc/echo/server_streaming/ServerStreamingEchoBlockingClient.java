package com.example.grpc.echo.server_streaming;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import com.example.grpc.echo.Logging;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ServerStreamingEchoBlockingClient {

    public static void main(String[] args) throws InterruptedException {
        Logging.init();

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        try {
            EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
            EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
            Iterator<EchoResponse> responses = blockingStub.serverStreamingEcho(request);

            while (responses.hasNext()) {
                System.out.println("response: " + responses.next().getMessage());
            }
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
