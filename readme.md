### Introduction to gRPC for Java developers


#### What is gRPC?

gRPC is a multi-language and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed for high-performance inter-service communication - whether on-premises, in the cloud, in containers, in mobile and IoT devices, or in browsers.

gRPC uses HTTP/2 as a transport protocol along with Protocol Buffers (Protobuf) as a binary serialization framework and RPC interface description language. Thanks to these features, gRPC can provide qualitative and quantitative characteristics of communication that are not available for RESTful services, which typically means transferring textual JSONs over the HTTP/1.1 protocol.


#### Why not REST?

RPC (Remote Procedure Call) is a distinct architectural style for building inter-service communication, quite different from REST (Representational State Transfer). REST is an architectural style based on the concept of resources. A resource is identified by a URI, and clients can create, read, update, or delete the *state* of the resource by *transferring* its *representation*.

However, with REST architecture, problems arise when implementing client-server interaction that go beyond client-initiated reading or writing of the state of a single resource, for example:



* Reading and writing complex data structures comprising multiple resources.
* Low-latency and high-throughput communication.
* Client streaming or bidirectional streaming.

RPC is based on the technique of calling methods in another process (either on the same machine or on a different machine over the network) as if they were local methods. RPC frameworks provide code generation tools that create client and server stubs based on a given RPC interface. These stubs handle message serialization and network communication. As a result, when a client invokes a remote method with parameters and receives a return value, it appears to be a local method call. RPC frameworks aim to hide the complexity of serialization and communication from developers. (However, developers using RPC should be aware that the network is inherently [unreliable](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing) and should implement retry/deadline/cancellation and exception handling to manage partial and total network failures.)

![Remote Procedure Call](/images/Remote_Procedure_Call.png)


#### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, it is necessary to ensure efficient cross-language and cross-platform serialization. Solutions, based on textual formats (such as JSON, YAML, or XML), are typically an order of magnitude less efficient than binary formats. They require additional computational overhead for serialization and additional network bandwidth for transmitting larger messages. To reduce the size of transmitted messages, there is no alternative to using binary formats. (However, these are not portable between different languages and platforms, and ensuring backward and especially forward compatibility presents significant challenges.)

Second, there was an absence of an efficient application-layer protocol specifically designed for modern inter-service communication. Initially, the HTTP protocol was designed to allow clients (typically browsers) to request resources such as HTML documents, images, and scripts from servers in the hypermedia systems. It was not designed to support high-speed, bidirectional, simultaneous communication. Various workarounds based on HTTP/1.0 (such as short polling, long polling, and webhooks) were inherently inefficient in their utilization of computational and network resources. Even new features introduced in HTTP/1.1 (persistent connections, pipelining, and chunked transfer encoding) proved insufficient for these purposes. (Perhaps only the TCP transport-layer protocol would have provided performant full-duplex communication, but it is too low-level to implement an efficient RPC framework based on it.)


#### The solution

Since 2001, Google has been developing an internal RPC framework named Stubby. It was designed to connect almost all internal services, both within and across Google data centers. Stubby was a high-performance multi-language and cross-platform framework built on Protobuf for serialization.

Only in 2015, with the emergence of the innovative HTTP/2 protocol, Google decided to enhance its features in a redesigned version of Stubby. References to Google's internal infrastructure (mainly name resolution and load balancing) were removed from the framework, and the project was redesigned to comply with open source standards. The framework has also been adapted for use in cloud-native applications and in resource-constrained mobile and IoT devices. This updated version was released as gRPC (which recursively stands for **g**RPC **R**emote **P**rocedure **C**alls).

Today, gRPC remains the primary mechanism for inter-service communication at Google. Also, Google offers gRPC interfaces alongside REST interfaces for many of its public services. This is because gRPC provides significant performance benefits and supports bidirectional streaming - a feature that is not achievable with traditional RESTful services.


#### gRPC foundations

The gRPC framework includes two main components:



* HTTP/2 - an application-layer protocol used as a transport protocol
* Protocol Buffers - a serialization framework and RPC interface definition language

![gRPC life cycle](/images/gRPC_life_cycle.png)


##### HTTP/2

HTTP/2 is an intermediate version of the HTTP application-layer protocol. HTTP/2 started as an internal Google project named SPDY in 2009, whose main design goal was to reduce latency on the Web. HTTP/2 retains the semantics of the previous version of the protocol (such as methods, response codes, headers) but introduces significant changes in implementation. While HTTP/2 brings several changes that benefit various platforms (browsers and mobile devices), only some of these improvements are relevant to gRPC.

The first improvement is multiplexing, which allows multiple concurrent requests and responses to be sent over a single TCP connection. This solves the HTTP *head-of-line blocking* problem, where a slow response to one request delays subsequent requests on the same connection. In HTTP/2, requests and responses are divided into frames that can be transmitted independently of each other within a stream. This approach allowed efficient streaming from client to server, from server to client, and simultaneous bidirectional streaming.

The second improvement is the transition from text-based headers and bodies to a binary format. The binary framing layer encodes all communication between the client and server (headers, data, control, and other frame types) into a binary format. This approach reduces the number of bytes transmitted over the network and lowers computational overhead for encoding.

The third improvement is header compression using the HPACK algorithm, which uses static and dynamic header tables together with Huffman encoding to reduce redundancy. This is particularly beneficial when multiple consecutive requests and responses share the same headers (which is common in inter-service communication) because it significantly reduces the number of transmitted bytes.


##### Protocol Buffers

Protocol Buffers (Protobuf) is a multi-language serialization framework and RPC interface definition language for effective data exchange over the network. By default, gRPC uses Protobuf to describe the RPC service contract, including methods exposed by the server, and the structure of request and response messages. This contract is strongly typed and explicitly designed to support backward and forward compatibility.

As a serialization framework, Protobuf is designed to encode structured data (which is common for object-oriented languages) into a compact binary format. The resulting binary messages are efficient not only for transmission over the network, but also for persistent storage. Protobuf is highly optimized to minimize network overhead by reducing the serialized message size. However, if developers have to minimize computational and memory overhead at the expense of increased message size, they can use gRPC with zero-copy serialization frameworks - FlatBuffers or Cap’n Proto.

As an interface definition language (IDL), the Protobuf compiler with the language-specific plugin generates client and service stubs from declared RPC services, which developers should use to implement their applications. The Protobuf compiler provides language-specific runtime libraries that transparently handle binary serialization and transmission of messages over the network.

Streaming is one of the most important features of gRPC, enabled by the underlying HTTP/2 protocol. Depending on whether the client sends a single parameter or a stream of parameters, and whether the service returns a single response or a stream of responses, there are four supported method types:



* Unary: the client sends a single request, and the server replies with a single response.
* Server-side streaming: the client sends a single request, and the server replies with multiple responses.
* Client-side streaming: the client sends multiple requests, and the server replies with a single response.
* Bidirectional streaming: both the client and server exchange multiple requests and responses simultaneously.


#### gRPC in practice

The following example demonstrates how to build a simple server-streaming gRPC application using plain Java. The application consists of an echo client that sends one or many requests, and an echo server that receives those requests, modifies them, and returns responses. The client receives the responses and displays them. (Client and server examples using the other method types are available in the GitHub [repository](https://github.com/alexander-linden/grpc-java-examples).)

![gRPC server-side streaming](/images/gRPC_server_side_streaming.png)

To implement this application, complete the following steps:



1. Define an RPC service interface in a *.proto* file.
2. Generate server and client stubs using the Protobuf compiler.
3. Implement a server that provides this service.
4. Implement a client that consumes this service.


##### The contract between the service and the client

A *.proto* file defines the contract between a client and a service. This example shows the *.proto* file used by both clients and servers in the application. Beyond the message and service definitions, the file also contains additional metadata. The *syntax* option defines the use of Protobuf version 3 (by default, version 2 with limited capabilities is used for backward compatibility). The *package* option defines the global cross-language Protobuf namespace. Also, each language may have its own Protobuf options. For Java, the *java_package* option defines the package where the generated Java classes are placed, and the *java_multiple_files = true* option defines generating separate Java files for each message and service defined in the *.proto* file.


```
// syntax
syntax = "proto3";

// package
package example.grpc;

// options
option java_package = "com.example.grpc";
option java_multiple_files = true;

// messages
message EchoRequest {
  string message = 1;
}

message EchoResponse {
  string message = 1;
}

// service
service EchoService {
  rpc UnaryEcho(EchoRequest) returns (EchoResponse);
  rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse);
  rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse);
  rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse);
}
```



##### Generating service and client stubs

To use gRPC in your Gradle project, place your *.proto* file in the *src/main/proto* directory, add the required implementation and runtime dependencies, and configure the Protobuf Gradle plugin. Next, execute the Gradle task *generateProto*, and the generated Java classes will be placed in a designated directory (in our example, *build/generated/source/proto/main/java*). These generated classes fall into two categories: message classes and service classes.

For the `EchoRequest` message, an immutable `EchoRequest` class is generated to handle data storage and serialization, along with an inner `EchoRequest.Builder` class to create the `EchoRequest` class using the Builder pattern. Similar classes are generated for the `EchoResponse` message.

For the `EchoService` service, an `EchoServiceGrpc` class is generated, containing inner classes for both providing and consuming the remote service. For the server-side, an abstract inner class `EchoServiceImplBase` is generated as the server stub, which you should extend to provide the service logic. For the client-side, four types of client stubs are generated:



* `EchoServiceStub`: to make asynchronous calls using the `StreamObserver` interface (it supports all four communication patterns)
* `EchoServiceBlockingStub`: to make synchronous calls (it supports unary and server-streaming calls only)
* `EchoServiceBlockingV2Stub`: to make synchronous calls (it supports unary calls as a stable feature and all 3 streaming calls as experimental features), but can throw checked `StatusException` instead of runtime `StatusRuntimeException`.
* `EchoServiceFutureStub`: to asynchronous calls with the `ListenableFuture` interface (it supports unary calls only)


##### Creating the server

The next step in the application implementation is to create an echo server. To implement a server that provides this service, complete the following steps:



1. Override the service methods in the generated service stub.
2. Start a server to listen for client requests.

We create the `EchoServiceImpl` class that extends the auto-generated abstract `EchoServiceGrpc.ServiceImplBase` class. The class overrides the `serverStreamingEcho` method, which receives the request as an `EchoRequest` instance to read from, and a provided `EchoResponse` stream observer to write to.

To process a client request, the server performs the following steps. For each message, it constructs an `EchoResponse` using the builder and sends it to the client by calling the `onNext` method. After all messages have been sent, the server calls the `onCompleted` method to indicate that the call has finished successfully. (Had an error occurred while processing the response, the server would have called the `onError` method to indicate that the call finished exceptionally.)


```
private static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
   @Override
   public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
       var name = request.getMessage();
       logger.log(Level.INFO, "request: {0}", name);

       responseObserver.onNext(EchoResponse.newBuilder().setMessage("hello " + name).build());
       responseObserver.onNext(EchoResponse.newBuilder().setMessage("guten tag " + name).build());
       responseObserver.onNext(EchoResponse.newBuilder().setMessage("bonjour " + name).build());
       responseObserver.onCompleted();
   }
}
```



To implement a server that provides this service, use the `ServerBuilder` class. First, specify the port to listen for client requests by calling the `forPort` method. Next, create an instance of the `EchoServiceImpl` service and register it in the server using the `addService` method (a server can provide multiple services). Finally, build and start the server using a modified version of the Netty server.


```
var server = ServerBuilder
   .forPort(50051)
   .addService(new EchoServiceImpl())
   .build()
   .start();

logger.log(Level.INFO, "server started, listening on {0,number,#}", server.getPort());

Runtime.getRuntime().addShutdownHook(new Thread(() -> {
   System.err.println("server is shutting down");
   try {
       server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
   } catch (InterruptedException e) {
       server.shutdownNow();
   }
   System.err.println("server has been shut down");
}));

server.awaitTermination();
```



##### Creating the client

The next step in the application implementation is to create an echo client. To implement a client that consumes this service, complete the following steps:



* Create a channel to connect to the service.
* Obtain a client stub of the required type.
* Invoke the service method using the obtained client stub.

We create a channel using the `ManagedChannelBuilder` class, specifying the server host and port we want to connect to. In the first client example, a blocking stub is used. This stub is obtained from the generated `EchoServiceGrpc` class by calling the `newBlockingStub` factory method and passing the channel as an argument. With this approach, the client blocks while invoking the `serverStreamingEcho` method and waits for the server’s response. The call either returns a response from the server or throws a `StatusRuntimeException`, in which a gRPC error is encoded as a `Status`.

The example below demonstrates a client for the server-side streaming service with a blocking stub, where the request is provided as a method parameter, and the response is returned as an iterator. After the call is completed, the channel is shut down to ensure that the underlying resources (threads and TCP connections) are released.


```
var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

try {
    var blockingStub = EchoServiceGrpc.newBlockingStub(channel)
       .withWaitForReady()
       .withDeadline(Deadline.after(30, TimeUnit.SECONDS));

   var request = EchoRequest.newBuilder().setMessage("world").build();
   var responses = blockingStub.serverStreamingEcho(request);
   while (responses.hasNext()) {
       logger.log(Level.INFO, "response: {0}", responses.next().getMessage());
   }
} catch (StatusRuntimeException e) {
    logger.log(Level.WARNING, "error: {0}", e.getStatus());
} finally {
   channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
}
```


In the second client example, we demonstrate the use of the same server-streaming service with an asynchronous, non-blocking stub. This stub is obtained from the same auto-generated `EchoServiceGrpc` class by calling the `newStub` factory method. As in the previous example, the request is provided as the first method parameter. The response is handled through a stream observer, which the client implements and passes as the second method parameter.

The `onNext` method is called each time the client receives a response from the server. The `onError` method can be called once if the call has finished exceptionally. The `onCompleted` method is invoked once after the server has successfully sent all responses and the call has finished successfully.


```
var channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

var asyncStub = EchoServiceGrpc.newStub(channel);
var request = EchoRequest.newBuilder().setMessage("world").build();

var done = new CountDownLatch(1);
asyncStub.serverStreamingEcho(request, new StreamObserver<EchoResponse>() {
   @Override
   public void onNext(EchoResponse response) {
       logger.log(Level.INFO, "next: {0}", response.getMessage());
   }

   @Override
   public void onError(Throwable t) {
       logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
       done.countDown();
   }

   @Override
   public void onCompleted() {
       logger.info("completed");
       done.countDown();
   }
});

done.await();
channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
```


In this implementation, the client does not block on the `serverStreamingEcho` method. To wait for the asynchronous interaction to complete (either successfully or with an exception) we use a `CountDownLatch` as a thread barrier. The main thread will be blocked until the `countDown` method is called, which occurs in either the `onError` or `onCompleted` handler of the response stream observer.


##### Running the server and client

To build the application, run the Gradle *shadowJar* task to produce a self-contained (über) JAR. Then, start the client and server in any order. Because the client stub is configured to wait for server readiness, it will wait until the server becomes available or the specified deadline is reached.

After the client has sent a request to the server and received a response from it, the client closes the channel and shuts itself down. To stop the server, press Ctrl+C to send a SIGINT signal to it. The server then shuts down gracefully as the JVM executes its registered shutdown hooks. We use logging to *stderr* here since the logger may have been reset by its JVM shutdown hook.


#### Conclusion

gRPC is an effective framework for implementing inter-service communication. However, like any technology, it is not a universal solution and is designed to address specific problems. You should consider migrating your application from REST to gRPC if it meets most of the following criteria:



* The application has performance requirements, including high throughput and low latency.
* The application requires client streaming or bidirectional streaming, which cannot be efficiently implemented using HTTP/1.1.
* Automatic generation of gRPC service and client stubs is available for all required languages and platforms.
* Both the client and server are developed within the same organization, and the application operates in a controlled environment.
* Your organization has strict engineering standards that require strongly defined client-server contracts.
* Development will benefit from built-in gRPC features, such as retry/deadline/cancellation, manual flow control, error propagation, interceptors, authentication, name resolution, client-side load balancing, health checking, proxyless service mesh, etc.

However, REST is a more appropriate architecture if the application meets most of the following conditions:



* The application is simple and operates under low loads, and there is simply no need to increase its performance.
* The application uses unary requests/responses and does not require streaming. (Or the application *does* use streaming using the WebSockets protocol, but you consider this does not violate the REST architecture.)
* Requests to the server are made directly from a browser, but using the gRPC-Web proxy is not technically justified.
* The application exposes a public API designed for consumption by a broad audience of external developers beyond your organization.
* Your organization can achieve successful backward and forward compatibility and versioning without strict constraints.

As a rule of thumb, you should migrate your RESTful services to gRPC when you need high-performance inter-service communication, especially with client streaming or bidirectional streaming.
