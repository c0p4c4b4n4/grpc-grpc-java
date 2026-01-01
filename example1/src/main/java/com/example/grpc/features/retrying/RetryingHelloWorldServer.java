package com.example.grpc.features.retrying;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoResponse;
import com.example.grpc.echo.EchoServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A HelloWorld server that responds to requests with UNAVAILABLE with a given percentage.
 */
public class RetryingHelloWorldServer {
  private static final Logger logger = Logger.getLogger(RetryingHelloWorldServer.class.getName());
  private static final float UNAVAILABLE_PERCENTAGE = 0.5F;
  private static final Random random = new Random();

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
        .addService(new GreeterImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);

    DecimalFormat df = new DecimalFormat("#%");
    logger.info("Responding as UNAVAILABLE to " + df.format(UNAVAILABLE_PERCENTAGE) + " requests");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          RetryingHelloWorldServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final RetryingHelloWorldServer server = new RetryingHelloWorldServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class GreeterImpl extends EchoServiceGrpc.EchoServiceImplBase {
    AtomicInteger retryCounter = new AtomicInteger(0);

    @Override
    public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
      int count = retryCounter.incrementAndGet();
      if (random.nextFloat() < UNAVAILABLE_PERCENTAGE) {
        logger.info("Returning stubbed UNAVAILABLE error. count: " + count);
        responseObserver.onError(Status.UNAVAILABLE
            .withDescription("Greeter temporarily unavailable...").asRuntimeException());
      } else {
        logger.info("Returning successful Hello response, count: " + count);
        EchoResponse reply = EchoResponse.newBuilder().setMessage("Hello " + request.getMessage()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
      }
    }
  }
}
