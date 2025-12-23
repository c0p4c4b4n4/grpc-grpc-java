### Introduction to gRPC Remote Procedure Calls


#### Introduction


When a seasoned software developer decides which technology to use for a new project, they will most likely choose the one they know best. This is the right decision in most cases, except when a new technology offers capabilities that were previously unavailable. In such situations, the time and effort spent learning something new will be justified and paid off.


This article provides developers with an introduction to the gRPC framework. In the first part, we explain what it is, how it originated, and which problems and how  it solves. The second part presents the main code patterns for using the gRPC framework in Java clients and servers.


#### What is gRPC

gRPC is a multi-language and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service interaction within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as a binary serialization format and RPC interface description language, and HTTP/2 as the transport layer protocol. Due to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available to REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


#### Why not REST ?

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



* Protocol Buffers - a multi-language, cross-platform serialization framework
* IDL (Interface Definition Language) - an extension of Protocol Buffers for defining RPC interfaces
* HTTP/2 - an application-level protocol


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


*Types* contain *scalar* types - 32/64 bits integers, 32/64 bits floating-point numbers, boolean, strings (UTF-8 encoded or 7-bit ASCII text), and bytes - and *composite* types - enumerations, structures, maps, and arrays. Interestingly, the type system contains a few types for describing integer data. They allow developers to choose a more compact type depending on whether the number is signed or unsigned and whether these values ​​are mostly small or uniformly distributed across the entire range.

*Names* are intended for developer understanding and are not included in the binary message.

*Identifiers* are used to uniquely identify field values in a binary message. These unique identifiers play a crucial role in ensuring backward and forward compatibility during the evolution of Protobuff messages. *Backward* compatibility means that newer code will read older messages. *Forward* compatibility means that older code will read newer messages: new fields that are not present in the old schema will be ignored, and old fields that are deleted in the new schema will have reasonable default values.

*Attributes* are additional metadata that can be added to messages. Previous versions of Protobuf had various attributes, but in version 3, only two remain: *optional* and *repeated*. The *optional* attribute is used to determine whether it was explicitly set or not, even if it's set to its default value. The *repeated* attribute is used to describe arrays.


##### Interface Definition Language

The interface description language is designed to describe the interface of RPC methods. Like message descriptions, interface descriptions are stored in a file with the *.proto* extension. This file, using the Protobuf compiler, converts pseudocode into client and server stubs in the selected programming language.

Depending on whether the method sends a single value or a stream and whether it returns a single value or a stream, there are 4 possible method types:



* *unary*: a simple call in which a client sends a single request and a server replies with a single response
* *server-side streaming*: a call in which a client sends a single request, but the server replies with multiple responses
* *client-side streaming*: a call in which a client sends multiple requests and a server replies with a single response. The server can opt to wait for the entire stream of client requests to cease before processing and responding
* *bidirectional streaming*: the client and server both send multiple calls back and forth concurrently, enabling real-time communication (full-duplex)


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

The following simplified example shows how to use all four types of gRPC methods in Java applications. The example includes an echo of the client sending a string to the server, and an echo of the server responding to that string.

The project is a Gradle project that includes a minimal set of gRPC dependencies and plugins. The required *contract* between the client and the server is specified in the *.proto* file, which is located in the */src/main/proto* directory.


```
syntax = "proto3";
option java_multiple_files = true;
package example.grpc.echo;

message EchoRequest {
    string message = 1;
}

message EchoResponse {
    string message = 1;
}

service EchoService {
    rpc UnaryEcho(EchoRequest) returns (EchoResponse);
    rpc ServerStreamingEcho(EchoRequest) returns (stream EchoResponse);
    rpc ClientStreamingEcho(stream EchoRequest) returns (EchoResponse);
    rpc BidirectionalStreamingEcho(stream EchoRequest) returns (stream EchoResponse);
}
```


If you run the Gradle task *mvn protobuf:compile* (or just *mvn clean install*), the generated stubs for the client and server will be created in directory in the */target/generated-sources/protobuf* directory.

The API that the client and server use to manage gRPC flows is the *StreamObserver* interface. This interface represents the gRPC stream of messages. It is used by both the client and server implementations for sending or receiving messages. For outgoing messages, an observer is provided by the gRPC library to the application. For incoming messages, the application implements the observer and passes it to the gRPC library for receiving.


```
public interface StreamObserver<V> {
  void onNext(V value);
  void onError(Throwable t);
  void onCompleted();
}
```



##### Service

The generated server stub provides the following API.


```
public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver)
public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver)
public StreamObserver<EchoRequest> clientStreamingEcho(StreamObserver<EchoResponse> responseObserver)
public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver)
```


Notice that the signatures for unary and server-streaming methods are the same. A single request is received from the client, and the server sends its one or many responses by calling the *onNext* method on the *response observer*. The difference is that for the unary method, the server calls the *onNext* method exactly once, followed by the call of the *onCompleted* method. In the server-streaming method, the *onNext* method can be called multiple times before streaming ends by the server with a call to the *onCompleted* method.

>Using runtime behavior-based differences over compile-time method overloading keeps the API simple and uniform.

Similarly, the signatures for client-streaming and bidirectional-streaming methods are the same either. Since the client can always send multiple messages to a service, the service provides it with a *request observer*. In both cases, the client can send one or many requests by calling the *onNext* method on the *request observer*, followed by the call of the *onCompleted* method. The difference is, that for the client-streaming method, the server calls the *onNext* method on the *response observer* exactly once, immediately followed by the call of the *onCompleted* method. In the bidirectional-streaming method, the server calls the *onNext* method the *response observer* multiple times before the call of the *onCompleted* method.

>Since this is an echo example, the server's response always follows the client's request. In real bidirectional-streaming applications, client and server requests and responses can be in any order and can be finished by both the client and the server.

A *simplified* version of the server that provides the unary echo method looks something like this:


```
Server server = ServerBuilder.forPort(50051)
   .addService(new EchoServiceGrpc.EchoServiceImplBase() {
       @Override
       public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
           EchoResponse response = EchoResponse.newBuilder().setMessage("hello " + request.getMessage()).build();
           responseObserver.onNext(response);
           responseObserver.onCompleted();
       }
   })
   .build()
   .start();

server.awaitTermination();
```



##### Clients

Three types of client stubs are generated: asynchronous, blocking, and future.


###### Asynchronous client

The asynchronous stub is the primary stub type for working with the gRPC via the Java API. This stub implements all service definition methods, and its interface is completely identical to that of the service stub. The asynchronous stub operates entirely through callbacks outgoing and incoming stream observers.


```
public void unaryEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver)
public void serverStreamingEcho(EchoRequest request, StreamObserver<EchoResponse> responseObserver)
public StreamObserver<EchoRequest> clientStreamingEcho(StreamObserver<EchoResponse> responseObserver)
public StreamObserver<EchoRequest> bidirectionalStreamingEcho(StreamObserver<EchoResponse> responseObserver)
```


>Use asynchronous stub for high-performance applications that stream from server to client, from client to server, or full-duplex.

A *simplified* version of the asynchronous client that consumes the unary echo method looks something like this:


```
ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

EchoServiceGrpc.EchoServiceStub asyncStub = EchoServiceGrpc.newStub(channel);
EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
asyncStub.unaryEcho(request, new StreamObserver<EchoResponse>() {
   @Override
   public void onNext(EchoResponse response) {
       System.out.println("next: " + response.getMessage());
   }

   @Override
   public void onError(Throwable t) {
       System.out.println("error: " + t);
   }

   @Override
   public void onCompleted() {
       System.out.println("completed");
   }
});

channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
```



###### Blocking client

The blocking stub uses synchronous calls that block until the response from the service is available. Blocking stubs implement only unary and server-streaming methods in the service definition. Blocking stubs do not support client-streaming or bidirectional-streaming methods.


```
public EchoResponse unaryEcho(EchoRequest request)
public Iterator<EchoResponse> serverStreamingEcho(EchoRequest request)
```


>Use blocking stubs for simple applications where blocking calls are justified by simplicity and thread inefficiency is not a concern.

A *simplified* version of the blocking client that consumes the unary echo method looks something like this:


```
ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

EchoServiceGrpc.EchoServiceBlockingStub blockingStub = EchoServiceGrpc.newBlockingStub(channel);
EchoRequest request = EchoRequest.newBuilder().setMessage("world").build();
EchoResponse response = blockingStub.unaryEcho(request);
System.out.println("result: " + response.getMessage());

channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
```



###### Future client

The asynchronous stub uses asynchronous calls that wrap the result into the *com.google.common.util.concurrent.ListenableFuture* interface. Future stubs implement only unary methods in the service definition. Future stubs do not support any streaming calls.


```
public ListenableFuture<EchoResponse> unaryExample(EchoRequest request)
```


>Use asynchronous stubs for high-performance applications that use only unary request/response calls.

A *simplified* version of the future client that consumes the unary echo method looks something like this:


```
ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();

EchoServiceGrpc.EchoServiceFutureStub futureStub = EchoServiceGrpc.newFutureStub(channel);
ListenableFuture<EchoResponse> responseFuture = futureStub.unaryEcho(EchoRequest.newBuilder().setMessage("world").build());
Futures.addCallback(responseFuture, new FutureCallback<EchoResponse>() {
   @Override
   public void onSuccess(EchoResponse response) {
       System.out.println("success: " + response.getMessage());
   }

   @Override
   public void onFailure(Throwable t) {
       System.out.println("error: " + t);
   }
}, MoreExecutors.directExecutor());

channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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
* The application has unary requests/responses and does not use streaming. (Or the application *does* use streaming, but you consider that using the WebSockets protocol and an application protocol on top of it does not violate the REST architecture.)
* Requests to the server are made directly from a browser, but using the gRPC-Web proxy is not technically justified.
* The application has an public API intended for use by a large number of consumers outside your organization. (The technical level of these developers may vary, and some of them may have difficulty adopting HTTP/2 or debugging binary messages.)
* Your organization has proven engineering processes that guarantee successful backward and forward compatibility and versioning during the evolution of the application.

Regardless of whether or not you use gRPC, remember that you should make decisions based on technical requirements, not preconceptions. It may turn out that the best solution to your problem is neither option gRPC nor option REST, but something else entirely — such as GraphQL, a WebSocket-based framework (Socket.IO, RSocket, Spring WebFlux), or even a message-oriented solution.
