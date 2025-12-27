### Introduction to gRPC Remote Procedure Calls


#### Introduction

When seasoned software developers decide which technology to use for a new project, they will most likely choose the one they know the best. This is the right decision in most cases, except when a new technology offers capabilities that were previously unavailable. In such situations, the time and effort spent learning something new will be justified and paid off.

This article provides developers with an introduction to the gRPC framework. In the first part, we explain what it is, how it originated, and which problems and how  it solves. The second part presents the code fragments for using the gRPC framework in Java clients and servers.


#### What is gRPC

gRPC is a multi-language and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service interaction within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as a binary serialization format and RPC interface description language, and HTTP/2 as the transport layer protocol. Due to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available to REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


#### Why not REST?

RPC (Remote Procedure Call) is a different architectural style for building interservice interactions than REST (Representational State Transfer). REST is an architectural style that is based on the concept of *resources*. A resource is identified by an URI and clients read or write the *state* of the resource by *transferring* its *representation*.

However, challenges with REST architecture arise when implementing client-server interactions that go beyond the scope of a client initiating a read or write of the state of a single resource, such as:



* complex data structures including several resources
* low-latency and high-throughput communication
* client streaming or bidirectional streaming

The foundation of RPC is based on the idea of invoking methods on another process as if they were local methods. RPC frameworks provide code generation tools that, based on the provided interfaces, create stub implementations for the client and server that handle binary serialization and network transmission. So, when a client calls a remote method with parameters and receives a return value, it *looks* like a local call. RPC frameworks try to hide away all the complexity of serializing and network communication.

However, in RPC it is impossible to fully avert the intermediate network communication (because *network is unreliable by its nature*) because of:



* network bandwidth is limited so client have to minimize size of parameters and return values
* network latency exists so client have to use maximum timeouts on method calls
* network can fail, so client stub can throw an exception
* network can partially fail, so client have to use retries and server should be idempotent


#### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, developers needed to ensure efficient cross-platform serialization. Solutions, based on textual formats (such as XML, JSON, or YAML), are typically an order of magnitude less efficient than binary formats. They require additional computational resources for serialization and additional network resources for transmitting larger messages. Solutions based on binary formats, often face significant challenges in ensuring portability across different languages ​​and platforms.

Second, there was an absence of an efficient application-layer network protocol specifically designed for modern inter-service communication. The HTTP/1.1 protocol was originally designed for browsers to retrieve resources within the hypermedia networks. It was not designed to support high-speed, bidirectional, full-duplex communication. Various workarounds based on this protocol (short and long polling, streaming, webhooks) were inherently inefficient in their utilization of computational and network resources. Solutions built on the TCP transport layer protocol were overly complex due to the low-level nature of the protocol and the lack of portability across different languages ​​and platforms.


#### The solution

Since 2001, Google had been developing an internal RPC framework called Stubby. It was designed to connect almost all of the internal services both within and across Google data centers. Stubby was a high-performance, cross-platform framework built on Protocol Buffers for serialization.

But only in 2015, with the appearance of the breakthrough HTTP/2 protocol, Google decided to leverage its features in a redesigned version of Stubby. References to Google's internal infrastructure were removed from the framework, and the project was redesigned to comply with public open source standards. The framework has also been adapted for use in mobile, IoT, and cloud-native applications. This revamped version was released as gRPC (which recursively stands for *gRPC Remote Procedure Calls*).

Today, gRPC remains the primary mechanism for inter-service communication at Google. Also, Google offers gRPC interfaces alongside REST interfaces for many of its public services. This is because gRPC provides notable performance benefits and supports bidirectional streaming - a feature that is not achievable with traditional REST services.


#### gRPC foundations

The gRPC framework includes three main components:



* Protocol Buffers — a multi-language, cross-platform serialization framework
* IDL (Interface Definition Language) — an extension of Protocol Buffers for defining RPC interfaces
* HTTP/2 — an application-level protocol


##### Protocol Buffers

Protocol Buffers (Protobuf) is a serialization framework. It includes a compact binary serialization format *and* multi-language libraries. This framework is optimized for exchanging short messages that fit entirely within device memory (usually less than a few megabytes).

Messages are described in a file with the *.proto* extension. This file, using the Protobuf compiler, generates domain objects in the selected programming language. Also, Protobuf includes libraries for the conversion of these objects to and from the binary format.

Each message consists of fields with a *type*, *name*, and *identifier* (fields can also have optional *attributes*):


```
message Person {
    int32 id = 1;
    string name = 2;
    bool has_photo = 3;
}
```


*Types* contain *scalar* types — 32/64 bits integers, 32/64 bits floating-point numbers, boolean, strings (UTF-8 encoded or 7-bit ASCII text), and bytes — and *composite* types — enumerations, structures, maps, and arrays. Interestingly, the type system contains a few types for describing integer data. They allow developers to choose a more compact type depending on whether the number is signed or unsigned and whether these values ​​are mostly small or uniformly distributed across the entire range.

*Names* are intended for developer understanding and are not included in the binary message.

*Identifiers* are used to uniquely identify field values in a binary message. These unique identifiers play a crucial role in ensuring backward and forward compatibility during the evolution of Protobuff messages. *Backward* compatibility means that newer code will read older messages. *Forward* compatibility means that older code will read newer messages: new fields that are not present in the old schema will be ignored, and old fields that are deleted in the new schema will have reasonable default values.

*Attributes* are additional metadata that can be added to messages. Previous versions of Protobuf had various attributes, but in version 3, only two remain: *optional* and *repeated*. The *optional* attribute is used to determine whether it was explicitly set or not, even if it's set to its default value. The *repeated* attribute is used to describe arrays.


##### Interface Definition Language

The interface description language is designed to describe the interface of RPC methods. Like message descriptions, interface descriptions are stored in a file with the *.proto* extension. This file, using the Protobuf compiler, converts pseudocode into client and server stubs in the selected programming language.

Depending on whether the method sends a single value or a stream and whether it returns a single value or a stream, there are 4 possible *communication patterns*:



* *unary*: a call in which a client sends a single request and a server replies with a single response
* *server-side streaming*: a call in which a client sends a single request, and the server replies with multiple responses
* *client-side streaming*: a call in which a client sends multiple requests and a server replies with a single response.
* *bidirectional streaming*: the client and server both send multiple calls back and forth concurrently, enabling full-duplex communication.


```
service EchoService {
    rpc UnaryEcho(EchoRequest) returns (EchoResponse);
    rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse);
    rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse);
    rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse);
}
```


>For backend developers who have long and unsuccessfully tried to implement simultaneous bidirectional inter-service communication with HTTP/1.1, it will be important to note that gRPC allows streaming from server to client, from client to server, and bidirectional simultaneous streaming.


##### HTTP/2

HTTP/2 is the next version of the HTTP transport protocol. Initially this protocol was developed to allow a client (usually a browser) to request resources (HTML documents, images, scripts) from a server in a hypermedia network. However, using this protocol to implement modern client-server systems (simultaneous bi-directional streaming) leads to complex and inefficient solutions. Even developing new features in HTTP/1.1 (persistent connections, pipelining, chunked transfer encoding) was not adequate.

HTTP/2 retains the semantics of the previous version of the protocol (methods, response codes, headers), but has significant implementation changes. HTTP/2 introduces several changes important for different environments (browsers, mobile, IoT), but only a few of them are important for gRPC.

The first important change is *multiplexing*, or the ability to send multiple concurrent requests/responses over a single TCP connection. This solves the problem of the HTTP *head-of-line blocking*, where a slow response to a previous request slows down subsequent requests transmitted over the same TCP connection. This reduced latency and allowed the use of fewer TCP connections. Requests and responses are broken into frames (small data fragments), which are transmitted interleaved in a HTTP/2 stream independently of each other. Multiplexing enables bidirectional, simultaneous data streaming between client and server.

>Multiplexing is the ability to send multiple concurrent streams on a single connection. gRPC uses channels to enable multiple streams over those multiple connections. Messages are sent as HTTP/2 data frames, each of which might contain multiple gRPC messages.

The second important change is the transition from a text-based format of headers and bodies of requests/responses to encoded ones. It involves using a binary format for request and response bodies and header compression (HPACK). If multiple requests and responses in a row share the same headers (which is common in inter-service interactions), the amount of bytes transferred for headers and their values ​​can be reduced by using dictionaries and Huffman encoding.

>HTTP/2 allows services to efficiently exchange information both in various simplex (unidirectional) modes and using a full-duplex (bidirectional) connection with simultaneous transmission of messages.


#### gRPC in practice

The following example shows how to implement a simple gRPC application in the Java programming language. The application consists of an echo client that sends one or many requests, and an echo server that receives those requests, modifies them, and sends them back. The client receives these responses and displays them.

To implement this, you need to do the following:



1. define a remote service in a .proto file
2. generate server and client stubs using the Protobuf compiler
3. implement a server that provides this service
4. implement a client that consumes this service


##### The contract

A *.proto* file is a *contract* between a service and a client, consisting of the following distinct sections:

*Syntax definition* to specify the version of the *proto* language being used.

*Package* to declare the namespace for the definitions to prevent naming conflicts.

*Imports* to allow the inclusion of definitions from other proto files, which is useful for their reusing.

*Options* to provide instructions to the protoc compiler on how to generate the code for various programming languages. <sup>Options can be applied at different scopes: file, message, field, enum, enum value, and service.</sup>

*Messages* to define data structures.

*Services* to define RPC services and the methods the service provides.

Here is the *.proto* file used in this example:


```protobuf
// syntax definition
syntax = "proto3";

// package
package com.sample.grpc.echo;

// imports
import "another.proto";

// options
option java_package = "com.example.grpc.echo";
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
  rpc UnaryEcho(EchoRequest) returns (EchoResponse) ;
  rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse) ;
  rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse) ;
  rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse) ;
}
```


>You specify a server-side streaming method by placing the *stream* keyword before the response type and a client-side streaming method by placing the *stream* keyword before the request type.

>The *java_package* option overrides the package for the generated Java classes over the *package* keyword. If code is generated in another language, the *java_package* parameter will have no effect.


##### Generating server and client stubs

To use the gRPC in your Gradle project, put your *.proto* files in the *src/main/proto* directory, add the necessary Gradle dependencies, and configure the Protobuf Gradle plugin:

// use properties


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


Then, run a Gradle task (*./gradlew generateProto* or *./gradlew compileJava* or *./gradlew build*) and the generated Java classes will be placed under a determined folder (in our example *build/generated/source/proto/main/java*). The generated classes fall into two main categories: those for the message definitions and those specific to the service definition.

For the EchoRequest message defined in the *.proto* file will be generated the immutable EchoRequest class for storing and serializing/deserializing these messages, and its inner EchoRequest.Builder class to create the messages using the Builder pattern. Similar classes will also be created for the EchoResponse message.

For the EchoService service defined in the *.proto* file, an EchoServiceGrpc class file will be generated, containing classes for providing and consuming this remote service. For *providing* this service, a server stub will be generated – an abstract inner class EcoServiceImplBase, which you must implement on the server to provide the remote service. For *consuming* this service, three different client stubs will be generated. The inner EcoServiceStub class you should use to make asynchronous remote calls using the StreamObserver interface (it supports all four communication patterns). The inner EcoServiceBlockingStub class you should use to make synchronous remote calls (it supports only unary and server-streaming calls). The inner EcoServiceFutureStub you should use to make asynchronous remote calls using the ListenableFuture interface (it supports only unary calls). And, the EchoServiceGrpc class contains static methods newStub, newBlockingStub, and newFutureStub for creating instances of the various client stubs.

Also, the EchoServiceGrpc class contains another blocking stub – the inner EcoServiceBlockingV2Stub class and the newBlockingV2Stub static method – if you want to use the checked StatusException exception instead of the non-checked StatusRuntimeException exception. Use this blocking stub if you want to ensure that potential gRPC errors will not be ignored, which may happen when using runtime exceptions.


##### Creating the server

The next step in implementing the application is creating an echo server. To implement a server that provides this service, you need to complete the following steps:



1. Override the service methods in the generated service stub.
2. Run a server to listen for the client requests and return the service responses.

This server has a EchoServiceImpl class that extends the generated EchoServiceGrpc.EchoServiceImplBase abstract class.This class overrides the serverStreamingEcho method that gets request as an EchoRequest instance when it should read from, and response as an *provided* instance of EchoResponse stream observer it should write to. In order to fulfill a client's request, we perform the following steps.For each message sent, we construct an EchoResponse using the builder. Then we use the response observer’s onNext() method to return this EchoResponse to the client. When all messages are sent, We use the response observer’s onCompleted() method to specify that we’ve finished the server-side streaming.


```
class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
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



To implement a gRPC server that provides this service, you need to use the ServerBuilder class. First, we specify the port we want to use to listen for client requests using the forPort() method of the builder. Then, we then create an EchoServiceImpl instance and add it to the of provided services using the addService() method of the builder. Finally, we build the server and start it using a modified version of the Netty server.


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

The next step in implementing the application is creating an echo client. To implement a client that consumes this service, you need to complete the following steps:



1. Create a channel to the service
2. Get a client stub for the required communication pattern
3. Call the service method on the obtained client stub

We create a channel using the ManagedChannelBuilder specifying the server host and port we want to connect (a gRPC channel is an abstraction on top of one or more HTTP/2 connections). In the first client example, we use a blocking stub, obtaining it from the auto-generated EchoServiceGrpc class using the newBlockingStub factory method and the channel as the parameter. In this case, the client will be blocked on the serverStreamingEcho method and wait for the server to respond, and will either return a response or raise an StatusRuntimeException (where a gRPC error will be encoded as a Status).

Since this is an example of using server-streaming via blocking stub, the request is passed as a method parameter, and the response is as an iterator returned from the method. Once the call over the channel is completed, we shut it down to prevent loss of the underlying network resources.


```
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
try {
   EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
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


In the second client example we demonstrate the use of the same server-streaming service method using a synchronous stub. Likewise, we obtain it from the auto-generated EchoServiceGrpc class using the newStub factory method. As with the previous example, the request is passed as the first method parameter. The difference is that the response is processed as a stream observer, which the client should implement and pass as the second method parameter.

The onNext(EchoResponse) method will be called each time when a client receives a single response from the server. The onError(Throwable) method will be called once when the call to the server ends with an error. The onCompleted() method will be called once when the call to the server has successfully sent all responses and finished the call.


```
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
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


In this case, the client will not be blocked on the serverStreamingEcho method. To wait for the asynchronous exchange to complete, either successfully or with an exception, we use a CountDownLatch as a thread barrier. The main thread will be blocked until the countDown method of the latch will be called once either in the onCompleted or in the onError handler of the stream observer.


##### Running the server and client

To build the application, run the Gladle command *./gradlew clean shadowJar*. Then start the server first and then the client.


```
java -cp build/libs/examples-all.jar com.example.grpc.echo.server_streaming.ServerStreamingEchoServer
java -cp build/libs/examples-all.jar com.example.grpc.echo.server_streaming.ServerStreamingEchoBlockingClient
```


To stop the server, press Ctrl-C to send the SIGINT signal and watch how the server shuts down as its JVM runs registered shutdown hooks (we use *stderr* here since the logger may have been reset by its JVM shutdown hook):


```
server is shutting down
server has been shut down
```



#### Conclusion

gRPC is an effective framework for developing inter-service communication. However, like any technology, it is not universal, but designed to solve a specific problem area.You should migrate your application from REST to gRPC if it meets most of the following conditions:



* The application is high-performance and requires high throughput and low latency.
* The application needs client-streaming or bidirectional-streaming, which is impossible to achieve using HTTP/1.1.
* For all required languages and platforms there is automatic generation of gRPC client and server stubs.
* The application server and client are developed within your organization, and the application will run in a controlled environment.
* Your organisation has strong engineering requirements that benefit from contracts between the client and server in *.proto* files

However, using REST is a more appropriate solution for the application if it meets most of the following conditions:



* The application is simple and has low loads, and increasing performance is not economically justified.
* The application has unary requests/responses and does not use streaming. (Or the application *does* use streaming, but you consider that using WebSockets does not violate the REST architecture)
* Requests to the server are made directly from a browser, but using the gRPC-Web proxy is not technically justified.
* The application has an public API intended for use by a large number of consumers outside your organization. (The technical level of these developers may vary, and some of them may have difficulty adopting HTTP/2 or debugging packed binary messages)
* Your organization has proven engineering processes that guarantee successful backward and forward compatibility and versioning during the evolution of the application.

Regardless of whether or not you use gRPC, remember that you should make decisions based on technical requirements, not preconceptions. It may turn out that the best solution to your problem is neither gRPC nor REST, but something else entirely — such as GraphQL, a WebSocket-based framework (Socket.IO, RSocket, Spring WebFlux — just to name a few), or even a message-oriented solution.
