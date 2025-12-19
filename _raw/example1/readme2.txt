gRPC provides different client stubs to accommodate various programming models: synchronous (blocking), asynchronous (non-blocking), and ListenableFuture (a non-blocking option in Java). 
Here are conceptual examples based on a gRPC service definition in a .proto file (using Java as an example language, where these three distinct stubs are available).
Prerequisites
Assume you have a service defined in a .proto file like this:
protobuf
service Greeter {
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

message HelloRequest {
  string name = 1;
}

message HelloReply {
  string message = 1;
}
gRPC generates corresponding client stubs (classes) from this definition. In Java, these typically are GreeterGrpc.GreeterBlockingStub, GreeterGrpc.GreeterStub (async), and GreeterGrpc.GreeterFutureStub (listenable future). 
1. Synchronous (Blocking) Client
The synchronous client call blocks the current thread until it receives a response from the server or encounters an error. This is the simplest model, behaving like a local method call. 
java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloReply;

public class BlockingClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext() // For demonstration, use a secure channel in production
                .build();

        // Create a blocking stub
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

        // Make the synchronous RPC call
        try {
            HelloRequest request = HelloRequest.newBuilder().setName("World").build();
            HelloReply response = blockingStub.sayHello(request);
            System.out.println("Received: " + response.getMessage());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
    }
}
2. Asynchronous (Non-blocking) Client with Callbacks 
The asynchronous client returns immediately and the response is handled via a separate StreamObserver interface with callback methods (onNext, onError, onCompleted). This allows the client to perform other tasks while waiting for the response. 
java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloReply;

public class AsyncClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create a non-blocking (async) stub
        GreeterGrpc.GreeterStub asyncStub = GreeterGrpc.newStub(channel);

        // Define a callback to handle the response
        StreamObserver<HelloReply> responseObserver = new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply reply) {
                System.out.println("Received: " + reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("RPC failed: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("RPC Completed");
            }
        };

        // Make the asynchronous RPC call
        HelloRequest request = HelloRequest.newBuilder().setName("World").build();
        asyncStub.sayHello(request, responseObserver);

        // Keep the main thread alive to receive the asynchronous response (in a real app, manage lifecycle properly)
        Thread.sleep(2000); 
        channel.shutdown();
    }
}
3. Asynchronous Client with ListenableFuture
This approach, primarily found in gRPC-Java, uses Guava's ListenableFuture. It sits between synchronous and callback-based async by returning a future object that can be waited upon (.get()) or to which a callback can be attached (Futures.addCallback()). 
java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloReply;
import java.util.concurrent.ExecutionException;

public class FutureClient {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create a listenable future stub
        GreeterGrpc.GreeterFutureStub futureStub = GreeterGrpc.newFutureStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("World").build();
        
        // Make the RPC call, which returns a ListenableFuture
        ListenableFuture<HelloReply> future = futureStub.sayHello(request);

        // Option A: Block and wait for the result (similar to sync)
        // HelloReply reply = future.get();
        // System.out.println("Received (blocking): " + reply.getMessage());

        // Option B: Attach a callback to execute when the future completes
        Futures.addCallback(future, new com.google.common.util.concurrent.FutureCallback<HelloReply>() {
            @Override
            public void onSuccess(HelloReply reply) {
                System.out.println("Received (callback): " + reply.getMessage());
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("RPC failed: " + t.getMessage());
            }
        }, MoreExecutors.directExecutor()); // Use an appropriate executor

        // Keep the main thread alive to receive the asynchronous response
        Thread.sleep(2000);
        channel.shutdown();
    }
}

