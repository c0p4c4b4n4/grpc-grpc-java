### Introduction to gRPC for Java developers


#### Introduction

This article will introduce Java developers to the gRPC framework. The first part will explain what it is, how it came about, and what problems it solves. The second part will demonstrate an example application using the gRPC framework in both client and server written in plain Java.


#### What is gRPC

gRPC is a multi-language and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service communication within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as a binary serialization format and RPC interface description language, and HTTP/2 as the application-layer protocol. Thanks to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available with REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


#### Why not REST?

RPC (Remote Procedure Call) is a different architectural style for building interservice communication than REST (Representational State Transfer). REST is an architectural style based on the concept of *resources*. A resource is identified by an URI, and clients can read or write the *state* of the resource by *transferring* its *representation*.

However, with REST architecture, problems arise when implementing client-server interaction that go beyond client-initiated reading or writing of the state of a single resource, for example:



* reading and writing complex data structures comprising multiple resources
* low-latency and high-throughput communication
* client streaming or bidirectional streaming

RPC is based on the technique of calling methods in another process as if they were local methods. RPC frameworks provide code generation tools that create client and server stubs based on a given RPC interface. These stubs handle data serialization and network communication. As a result, when a client calls a remote method with parameters and gets a return value, it looks like a local call. RPC frameworks aim to hide the complexity of serialization and network communication from developers.

However, it is not possible to completely hide the intermediate network communication in RPC, because [the network is unreliable by its nature](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing):



* the network bandwidth is limited, so client have to minimize the size of parameters and return values
* the network latency exists, so client have to use maximum timeouts when calling methods
* the network can fail, so client may throw a network-related exception
* the network can partially fail, so client have to use retries, and server should be idempotent


#### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, developers needed to ensure efficient cross-platform serialization. Solutions, based on textual formats (such as XML, JSON, or YAML), are typically an order of magnitude less efficient than binary formats. They require additional computational resources for serialization and additional network resources for transmitting larger messages. Solutions based on binary formats often face significant challenges in ensuring portability across different languages ​​and platforms.

Second, there was an absence of an efficient application-layer network protocol specifically designed for modern inter-service communication. The HTTP/1.1 protocol was originally designed for browsers to retrieve resources within the hypermedia networks. It was not designed to support high-speed, full-duplex communication. Various workarounds based on this protocol (short and long polling, streaming, webhooks) were inherently inefficient in their utilization of computational and network resources. Solutions built on the TCP transport-layer protocol were overly complex due to the low-level nature of the protocol and the lack of portability across different languages ​​and platforms.


#### The solution

Since 2001, Google had been developing an internal RPC framework called Stubby. It was designed to connect almost all of the internal services both within and across Google data centers. Stubby was a high-performance, cross-platform framework built on Protocol Buffers for serialization.

But only in 2015, with the advent of the innovative HTTP/2 protocol, Google decided to leverage its features in a redesigned version of Stubby. References to Google's internal infrastructure were removed from the framework, and the project was redesigned to comply with public open source standards. The framework has also been adapted for use in mobile, IoT, and cloud-native applications. This updated version was released as gRPC (which recursively stands for "gRPC Remote Procedure Calls").

Today, gRPC remains the primary mechanism for inter-service communication at Google. Also, Google offers gRPC interfaces alongside REST interfaces for many of its public services. This is because gRPC provides notable performance benefits and supports bidirectional streaming — a feature that is not achievable with traditional REST services.


#### gRPC foundations

The gRPC framework includes three main components:



* Protocol Buffers — a multi-language, cross-platform serialization framework
* IDL (Interface Definition Language) — an extension of Protocol Buffers for defining RPC interfaces
* HTTP/2 — an application-layer protocol


##### Protocol Buffers

Protocol Buffers (Protobuf) is a multi-language serialization framework designed to encode structured data into a compact binary format (also being developed by Google). The resulting binary messages are suitable not only for high-performance network transmission but also for persistent data storage. Each message consists of fields that have a required *type*, *name*, *identifier*, and fields can also include optional *attributes*. Messages are defined in *.proto* files, which are processed by the Protobuf compiler (*protoc*) to generate strongly typed domain objects in the target programming language. Protobuf also provides runtime libraries for each supported language that manage serialization between in-memory objects and the binary format.


```
message Person {
  // scalar types
  int32 id = 1;
  string name = 2;
  bytes photo = 3;
  bool is_verified = 2;

  // composite types
  enum Status {
    UNKNOWN = 1;
    ACTIVE = 2;
    INACTIVE = 3;
  }
  Status status = 5;

  message Address {
    string country = 1;
    string city = 2;
    string street = 3;
  }
  Address address = 6;

  map<string, string> phone_numbers = 7;
  repeated string email_addresses = 8;
}
```


*Types* include *scalar types* — 32/64-bit integers, 32/64-bit floating-point numbers, booleans, UTF-8 strings, and bytes, and *composite types* — enumerations, structures, maps, and arrays. Interestingly, the type system provides several integer types that allow developers to select the most space-efficient representation based on whether the values are positive, negative, or can include both, and whether they are typically small or distributed over the entire range:



* *int32 / int64* – for positive or negative integers, encoded using variable-length encoding (optimized for small values, but non-efficient for negative values)
* *uint32 / uint64* – for non-negative integers only, encoded using variable-length encoding (optimized for small values)
* *sint32 / sint64* – for positive or negative integers, encoded with [ZigZag encoding](https://en.wikipedia.org/wiki/Variable-length_quantity#Zigzag_encoding) (optimized for small negative values)
* *fixed32 / fixed64* – for non-negative integers only, encoded with fixed-width encoding (optimized for values that are uniformly distributed)
* *sfixed32 / sfixed64* – for positive or negative integers, encoded with fixed-width encoding (optimized for values that are uniformly distributed)

*Names* are intended for developer readability and are not included in the binary message.

*Identifiers* uniquely identify field values in a binary message. These identifiers are essential for maintaining backward and forward compatibility as messages evolve. *Backward compatibility* ensures that newer code will read older messages. Much complicated *forward compatibility* ensures that older code will read newer messages: new fields will be ignored, and removed fields will have reasonable default values.

*Attributes* provide additional metadata for fields. Previous versions of Protobuf supported several attributes, but in version 3 only *optional* and *repeated* remain. The *optional* attribute allows to track whether a field was explicitly set, even if it was assigned its default value. The *repeated* attribute is used to define arrays.


##### Interface Definition Language

The Interface Description Language (IDL) is used to define RPC method interfaces. Similar to message definitions, interface definitions are stored in *.proto* files. These files are processed by the Protobuf compiler, which generates client and server stubs in the chosen programming language from the defined interface.

Based on whether a method handles a single message or a stream of messages on the request and response sides, there are four supported *communication patterns*:



* *unary*: the client sends a single request, and the server replies with a single response
* *server-side streaming*: the client sends a single request, and the server server replies with multiple responses
* *client-side streaming*: the client sends multiple requests, and the server replies with a single response
* *bidirectional streaming*: both the client and server exchange multiple messages simultaneously, enabling full-duplex communication


```
service EchoService {
  rpc UnaryEcho(EchoRequest) returns (EchoResponse);
  rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse);
  rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse);
  rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse);
}
```



##### HTTP/2

HTTP/2 is the next version of the HTTP transport protocol. Initially the HTTP/0.9 protocol was designed to let clients (typically browsers) request resources such as HTML documents, images, and scripts from servers over a hypermedia network. However, using this protocol to implement modern client-server systems with simultaneous bi-directional streaming results in complex and inefficient solutions. Even new features introduced in HTTP/1.1 such as persistent connections, pipelining, and chunked transfer encoding — proved insufficient for these demands.

HTTP/2 retains the semantics of the previous version of the protocol (methods, response codes, headers), but introduces significant changes in implementation. While HTTP/2 brings several improvements that benefit various environments (browsers, mobile devices, and IoT) only a subset of these changes is relevant to gRPC.

The first important improvement is *multiplexing*, which allows multiple concurrent requests and responses to be sent over a single TCP connection. This solves the HTTP *head-of-line blocking* problem, where a slow response to one request delays subsequent requests on the same connection. Multiplexing reduces latency and enables the use of fewer TCP connections. In HTTP/2, requests and responses are divided into frames (small data fragments) that can be transmitted interleaved and independently of each other within a stream. This mechanism supports simultaneous bidirectional streaming between the client and server.

The second important improvement in HTTP/2 is the transition from the text-based request and response headers and bodies to *binary* format. The binary framing layer encodes all communication between the client and server (headers, data, settings, control, etc.) into a structured binary format. Headers are additionally compressed using the HPACK algorithm, which leverages static and dynamic tables along with Huffman encoding to reduce redundancy. This is particularly beneficial when multiple consecutive requests and responses share the same headers (a common scenario in inter-service communication) significantly reducing the number of bytes transmitted and improving overall network efficiency.


#### gRPC in practice

The following example demonstrates how to build a simple gRPC application using plain Java. The application consists of an echo client that sends one or many requests, and an echo server that receives those requests, modifies the data, and returns the response. The client receives the responses and displays them.

To implement this application, complete the following steps:



1. define a remote service in a *.proto* file
2. generate server and client stubs using the Protobuf compiler
3. implement a server that provides this service
4. implement a client that consumes this service


##### The contract between service and client

A *.proto* file defines the *contract *between a service and a client. A typical *.proto* file consists of the following sections:



* *syntax definition*: specifies the version of the Protbuf language being used
* *package*: declares a namespace for the definitions to avoid naming conflicts.
* *imports*: allows reuse of definitions from other *.proto* files.
* *options*: provides instructions to the Protbuf compiler for code generation across different programming languages. <sup>Options can be applied at different scopes: file, message, field, enum, enum value, and service.</sup>
* *messages*: defines the data structures exchanged between the client and the service.
* services: defines RPC services and the methods provided by the service. \


Below is the *.proto* file used in this example:


```
// syntax definition
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

// services
service EchoService {
  rpc UnaryEcho(EchoRequest) returns (EchoResponse);
  rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse);
  rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse);
  rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse);
```


}

>The *java_package* option overrides the package name used for the generated Java classes when a *package* declaration is also present in the *.proto* file.


##### Generating service and client stubs

To use gRPC in your Gradle project, place your *.proto* file in the *src/main/proto* directory, add the required Gradle dependencies, and [configure](https://www.google.com/) the Protobuf Gradle plugin.


```
plugins {
  id 'application'
  id 'com.google.protobuf' version '0.9.5'
}

def grpcVersion = '1.76.2'
def protobufVersion = '3.25.8'
def protocVersion = protobufVersion

dependencies {
  implementation "io.grpc:grpc-protobuf:${grpcVersion}"
  implementation "io.grpc:grpc-services:${grpcVersion}"
  implementation "io.grpc:grpc-stub:${grpcVersion}"

  runtimeOnly "io.grpc:grpc-netty-shaded:${grpcVersion}"
}

protobuf {
  protoc { 
    artifact = "com.google.protobuf:protoc:${protocVersion}" 
  }
  plugins {
    grpc { 
      artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}" 
    }
  }
  generateProtoTasks {
    all()*.plugins { 
      grpc {} 
    }
  }
}
```


Next, execute a Gradle task (*./gradlew generateProto* or *./gradlew compileJava* or just *./gradlew build*), and the generated Java classes will be placed in a designated directory (in our example *build/generated/source/proto/main/java*). These generated classes fall into two categories: message definition classes and service definition classes.

For the EchoRequest message, an immutable EchoRequest class is generated to handle data storage and serialization, along with an inner EchoRequest.Builder class to create the EchoRequest class using the builder pattern. Similar classes are generated for the EchoResponse message.

For the EchoService, a EchoServiceGrpc class is generated, containing inner classes for both *providing* and *consuming* the remote service. For the server-side, an abstract inner class EchoServiceImplBase is generated as the server stub, which you should extend and implement to provide the service logic. For the client-side, four types of client stubs are generated:



* EchoServiceStub: to make asynchronous calls using the StreamObserver interface (it supports all four communication patterns)
* EchoServiceBlockingStub: to make synchronous calls (it supports unary and server-streaming calls only)
* EcoServiceBlockingV2Stub : to make synchronous calls (it also supports unary and server-streaming calls only, but throws checked StatusException instead of the runtime StatusRuntimeException exceptions)<sup>Use this to ensure that potential gRPC errors will not be ignored, which may happen when using runtime exceptions.</sup>
* EchoServiceFutureStub: to asynchronous calls with the [ListenableFuture](https://javadoc.io/doc/com.google.guava/guava/latest/com/google/common/util/concurrent/ListenableFuture.html) interface (it supports unary calls only)

The [StreamObserver](https://grpc.github.io/grpc-java/javadoc/io/grpc/stub/StreamObserver.html) interface serves as the API for managing streaming between the client and service. It is used by both parties to send and receive messages. For outbound messages, the gRPC library provides an observer instance, and the participant invokes its methods to transmit messages. For inbound messages, the participant implements this interface and passes it to the gRPC library, which then calls the appropriate methods upon message reception.


##### Creating the server

The next step in the application implementation is to create an echo server. To implement a server that provides this service, complete the following steps:



1. Override the service methods in the generated service stub.
2. Start a server to listen for client requests.

We create the EchoServiceImpl class that extends and implements the generated EchoServiceGrpc.ServiceImplBase abstract class. The class overrides the serverStreamingEcho method, which receives the request as an EchoRequest instance to read from, and a *provided* EchoResponse stream observer to write responses to.

To process a client request, the server performs the following steps: for each message, it constructs an EchoResponse using the builder and sends it to the client by calling the response stream observer’s onNext method. After all messages have been sent, the server calls the response stream observer’s onCompleted method to indicate that server-side streaming has finished.


```
class EchoServiceImpl extends EchoServiceGrpcServiceImplBase {
   @Override
   public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
       logger.log(Level.INFO, "request: {0}", request.getMessage());
       for (int i = 1; i <= 7; i++) {
           String value = "hello " + request.getMessage() + " " + i;
           EchoResponse response = EchoResponse.newBuilder().setMessage(value).build();
           responseObserver.onNext(response);
       }
       responseObserver.onCompleted();
   }
}
```



To implement a gRPC server that provides this service, use the ServerBuilder class. First, specify the port to listen for client requests by calling the forPort method. Next, create an instance of the EchoServiceImpl service and register it with the server using the addService method. Finally, build and start the server using a modified version of the Netty server.


```
int port = 50051;
Server server = ServerBuilder.forPort(port)
   .addService(new EchoServiceImpl())
   .build()
   .start();

logger.log(Level.INFO, "server started, listening on {0}", port);

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
* Obtain a client stub for the required communication pattern.
* Invoke the service method using the obtained client stub.

We create a channel using the ManagedChannelBuilder class, specifying the server host and port we want to connect. In the first client example, a blocking stub is used. This stub is obtained from the auto-generated EchoServiceGrpc class by calling the newBlockingStub factory method and passing the channel as an argument. With this approach, the client *blocks* while invoking the serverStreamingEcho method and waits for the server’s response. The call either returns a response from the server or throws a StatusRuntimeException, in which a gRPC error is encoded as a Status.

Because this example demonstrates server-side streaming with a blocking stub, the request is provided as a method parameter, and the response is returned as an iterator. After the call is completed, the channel is shut down to ensure that the underlying network resources.


```
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
try {
   EchoServiceGrpcServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
   EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
   Iterator<EchoResponse> responses = blockingStub.serverStreamingEcho(request);
   while (responses.hasNext()) {
       logger.log(Level.INFO, "response: {0}", responses.next().getMessage());
   }
} catch (StatusRuntimeException e) {
    logger.log(Level.WARNING, "error: {0}", e.getStatus());
} finally {
   channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
}
```


In the second client example, we demonstrate the use of the same server-streaming service method with a synchronous, non-blocking stub. This stub is obtained from the auto-generated EchoServiceGrpc class by invoking the newStub factory method. As in the previous example, the request is provided as the first method parameter. The response is handled through a stream observer, which the client implements and passes as the second method parameter.

The onNext method is called each time the client receives a single response from the server. The onError method can be called once if the call has completed exceptionally. The onCompleted method is invoked once after the server has successfully sent all responses and the call has completed successfully.


```
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

EchoServiceGrpcServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();

CountDownLatch latch = new CountDownLatch(1);
asyncStub.serverStreamingEcho(request, new StreamObserver<EchoResponse>() {
   @Override
   public void onNext(EchoResponse response) {
       logger.log(Level.INFO, "next: {0}", response.getMessage());
   }

   @Override
   public void onError(Throwable t) {
       logger.log(Level.WARNING, "error: {0}", Status.fromThrowable(t));
       latch.countDown();
   }

   @Override
   public void onCompleted() {
       logger.info("completed");
       latch.countDown();
   }
});

latch.await();
channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
```


In this implementation, the client is not blocked on the serverStreamingEcho method. To wait for the asynchronous interaction to complete — either successfully or with an exception — we use a CountDownLatch as a thread barrier. The main thread will be blocked until the countDown method is called, which occurs in either the onCompleted or onError handler of the response stream observer.


##### Running the server and client

To build the application, execute the Gradle task *./gradlew clean shadowJar*. Then start the server first and then start the client:


```
java -cp build/libs/examples-all.jar com.example.grpc.server_streaming.ServerStreamingEchoServer
java -cp build/libs/examples-all.jar com.example.grpc.server_streaming.ServerStreamingEchoBlockingClient
```


To stop the server, press Ctrl+C to send a SIGINT signal. The server then shuts down gracefully as the JVM executes its registered shutdown hooks. We use *stderr* here since the logger may have been reset by its JVM shutdown hook:


```
server is shutting down
server has been shut down
```



#### Conclusion

gRPC is an effective framework for implementing inter-service communication. However, like any technology, it is not a universal solution and is designed to address specific problem domains. You should consider migrating your application from REST to gRPC if it meets most of the following criteria:



* The application has high performance requirements, including high throughput and low latency.
* The application requires client streaming or bidirectional streaming, which cannot be efficiently implemented using HTTP/1.1.
* Automatic generation of gRPC service and client stubs is available for all required programming languages and platforms.
* Both the client and server are developed within the same organization, and the application operates in a controlled environment.
* Your organization has strong engineering standards that benefit from clearly defined client–server contracts specified in *.proto* files.
* Developers benefit from built-in capabilities such as advanced request handling — including cancellation, deadlines, retries, flow control, and error handling — as well as authentication, load balancing, and health checking.

However, REST is a more appropriate architecture if the application meets most of the following conditions:



* The application is simple and operates under low loads, and increasing performance is not economically justified.
* The application uses unary requests/responses and does not require streaming. (Or the application *does* use streaming using WebSockets, but you consider this does not violate the REST architecture)
* Requests to the server are made directly from a browser, but using the gRPC-Web proxy is not technically justified.
* The application exposes a public API intended for a large number of consumers outside your organization. (The technical level of these developers may vary, and some of them may have difficulty adopting HTTP/2 or debugging packed binary messages).
* Your organization has proven  engineering processes that guarantee successful backward and forward compatibility and versioning during the evolution of the application.

As a rule of thumb, you should migrate your RESTful services to gRPC when you need high-performance inter-service communication — such as low-latency or high-throughput with simplex or full-duplex steaming — especially in microservice or distributed architectures that you control.
