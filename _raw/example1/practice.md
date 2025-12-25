#### gRPC in practice

The following example shows how to implement a simple gRPC application in the Java programming language. The application consists of an echo client that sends one or many requests, and an echo server that receives those requests, modifies them, and sends them back. The client receives one or many reponses and displays them.

To implement this, you need to do the following:



* define a RPC service in a .proto file
* generate server and client stubs using the Protobuff compiler
* implement a server that provides this service
* implement a client that consumes this service


##### The contract

A *.proto* file is a *contract* between a service and a client, consisting of the following distinct sections.

*Syntax definition*, that specified the version of the *proto* language being used.

*Package*, that declares the namespace for the definitions to prevent naming conflicts.

*Imports*, that allows the inclusion of definitions from other proto files, which is useful for their reusing.

*Options*, that provide instructions to the protoc compiler on how to generate the code for various programming languages. <sup>Options can be applied at different scopes: file, message, field, enum, enum value, and service.</sup>

*Messages*, that define data structures.

*Services*, that define RPC services and the methods the service provides.

Here is an example of a .proto file:


```
// syntax definition
syntax = "proto3";

// package
package com.sample.grpc;

// imports
import "another.proto";

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
* *Bidirectional streaming*, where both sides send a sequence of messages using a read-write stream. The two streams operate independently, so clients and servers can read and write messages in any order.

>The *java_package* option specifies the package you want to use for our generated Java classes. If no explicit *java_package* option is given in the .proto file, then by default the proto package (specified using the “package” keyword) will be used. If we generate code in another language from this .proto, the *java_package* option has no effect.


##### Generating client and server code

To use the gRPC in your Gradle project, put your *.proto* files in the *src/main/proto* directory, add the necessary Gradle dependencies and configure the Protobuff Gradle plugin.


```
implementation 'io.grpc:grpc-protobuf:1.78.0'
implementation 'io.grpc:grpc-stub:1.78.0'
runtimeOnly 'io.grpc:grpc-netty-shaded:1.78.0'

plugins {
    id 'com.google.protobuf' version '0.9.5'
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:3.25.8"
  }
  plugins {
    grpc {
      artifact = 'io.grpc:protoc-gen-grpc-java:1.78.0'
    }
  }
  generateProtoTasks {
    all()*.plugins {
      grpc {}
    }
  }
}
```


Then, execute a Gradle task (*./gradlew generateProto* or *./gradlew compileJava* or *./gradlew build*) and the generated Java classes will be placed under a determined package (in our example *build/generated/source/proto/main/java*).

If the .proto file contains the *java_multiple_files = true* option, then there will be generated the following classes. The generated classes fall into two main categories: those for the message definitions and those specific to the service definition.

For each message defined in a .proto file, the compiler generates the following classes:



* EchoRequest is a final class for populating, serializing, and retrieving EchoRequest messages.
* The EchoRequest.Builder class is an inner class used to construct EchoRequest instances using the builder pattern.

Both classes will be located in a single EchoRequest.java file.

The class EcoService generated from a .proto file does not exist by that specific name. Instead, the protoc compiler with the plugin generates a wrapper class for your service with a *Grpc* suffix. If you define a service named EcoService in a .proto file, the generated code contains a class named EcoServiceGrpc that contains the following key components (nested classes and static methods) for *providing* and *consuming* your service:



* EcoServiceImplBase: An abstract inner class that you must extend and implement in the server to provide the RPC service.
* Stubs for clients: Inner classes used by clients to make calls to the server:
    * EcoServiceStub: This is a client-side stub class used for making asynchronous (non-blocking) RPC calls.
    * EcoServiceBlockingStub: This is another client-side stub class used for making synchronous (blocking) RPC calls.
    * EcoServiceFutureStub: This is a client-side stub class used for making asynchronous (non-blocking) RPC calls using ListenableFuture.
* Static methods: Utility methods, such as newStub, newBlockingStub, and newFutureStub, for creating instances of the various client stubs.

The EcoServiceImplBase class will be located in a single EchoServiceGrpc.java file.


##### Creating the server

First let’s look at how we create an EchoService server. There are two parts to making our EchoService service do its job:



* Overriding the service base class generated from our service definition.
* Running a RPC server to listen for requests from clients and return the service responses.


###### Implement the service class

As you can see, our server has a EchoServiceImpl class that extends the generated EchoServiceGrpc.EchoServiceImplBase abstract class.This class overrides the serverStreamingEcho method as gets request as an EchoRequest instance when it should read from, and response as an provided instance of EchoResponse response observer it should write to. We get as many rsponses as we need to return to the client, and write them each in turn to the response observer using its onNext() method. Finally, we use the response observer’s onCompleted() method to tell the service that we’ve finished writing responses.


```
static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
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


The serverStreamingEcho method takes two parameters:



* EchoRequest: the request
* StreamObserver&lt;EchoResponse>: a response observer, which is a special interface for the server to call with its response.

To return our response to the client and complete the call:



1. We construct and populate an EchoResponseinstance to return to the client.
2. We use the response observer’s onNext() method to return the EchoResponse.
3. We use the response observer’s onCompleted() method to specify that we’ve finished dealing with the RPC call.

###### 
Starting the server


Once we’ve implemented all our methods, we also need to start up a gRPC server so that clients can actually use our service. The following snippet shows how we do this for our RouteGuide service:



```
int port = 50051;
Server server = ServerBuilder.forPort(port)
   .addService(new EchoServiceImpl())
   .build()
   .start();

logger.log(Level.INFO, "server started, listening on {0}", port);

server.awaitTermination();
```



As you can see, we build and start our server using a ServerBuilder. To do this, we:



1. Specify the address and port we want to use to listen for client requests using the builder’s forPort() method.
2. Create an instance of our service implementation class EchoServiceImpl and pass it to the builder’s addService() method.
3. Call build() and start() on the builder to create and start an RPC server for our service.
