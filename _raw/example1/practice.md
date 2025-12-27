<!-----



Conversion time: 0.982 seconds.


Using this Markdown file:

1. Paste this output into your source file.
2. See the notes and action items below regarding this conversion run.
3. Check the rendered output (headings, lists, code blocks, tables) for proper
   formatting and use a linkchecker before you publish this page.

Conversion notes:

* Docs™ to Markdown version 2.0β1
* Sat Dec 27 2025 07:06:47 GMT-0800 (PST)
* Source doc: g b
* This is a partial selection. Check to make sure intra-doc links work.
----->



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


```
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


>You specify a server-side streaming method by placing the *stream* keyword before the response type. You specify a client-side streaming method by placing the *stream* keyword before the request type.

There are four *communication patterns* that can be used in gRPC methods:



* *Unary*, where the client sends a single request to the server and waits for a single response to come back.
* *Server-side streaming*, where the client sends a request to the server and gets a stream to read a sequence of messages back. The client reads from the returned stream until there are no more messages.
* *Client-side streaming*, where the client writes a sequence of messages and sends them to the server, using a provided stream. Once the client has finished writing the messages, it waits for the server to read them all and return its response.
* *Bidirectional streaming*, where both sides send a sequence of messages using a read-write stream. The two streams operate independently, so clients and servers can read and write messages in any order. <sup>The order of messages in each stream is preserved.</sup>

>The *java_package* option specifies the package for the generated Java classes. If the *java_package* option is not explicitly specified in the *.proto* file, the package specified with the *package* keyword will be used by default. If code in another language is generated from this *.proto* file, the *java_package* option will have no effect.


##### Generating server and client stubs

To use the gRPC in your Gradle project, put your *.proto* files in the *src/main/proto* directory, add the necessary Gradle dependencies, and configure the Protobuf Gradle plugin:


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


###### Implement the service

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



###### 
Starting the server


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

We create a channel using the ManagedChannelBuilder specifying the server host and port we want to connect (a gRPC channel is an abstraction on top of one or more HTTP/2 connections). In the first client example, we use a blocking stub, obtaining it from the auto-generated EchoServiceGrpc class using the newBlockingStub factory method and the channel as the parameter. In this case, the client will be blocked on the serverStreamingEcho method and wait for the server to respond, and will either return a response or raise an exception.

We create and populate an EchoRequest instance, pass it to the serverStreamingEcho() method on our blocking stub, and get back an EchoResponse instance. If an error occurs, it is encoded as a Status, which we can obtain from the StatusRuntimeException. Next, let’s look at a server-side streaming call to serverStreamingEcho, which returns a stream of responses. The method returns an Iterator that the client can use to read all the returned EchoResponse.

Since this is an example of using server-streaming via blocking stub, the request is passed as a method parameter, and the response is as an iterator returned from the method. Once exchange over the channel is completed, we shut it down to prevent loss of the underlying network resources.


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


##### Running server and client

To build the application, run the Gladle command *./gradlew clean shadowJar*. Then start the server first and then the client.


```
java -cp build/libs/examples-all.jar com.example.grpc.echo.server_streaming.ServerStreamingEchoServer
java -cp build/libs/examples-all.jar com.example.grpc.echo.server_streaming.ServerStreamingEchoBlockingClient
```


To stop the server, press Ctrl-C to send the SIGINT signal and watch how the server shuts down as its JVM runs registered shutdown hooks:


```
server is shutting down
server has been shut down
```