#### gRPC in practice

The following example shows how to implement a simple gRPC application in the Java programming language. The application consists of an echo client that sends one or many requests, and an echo server that receives those requests, modifies them, and sends them back. The client receives these reponses and displays them.

To implement this, you need to do the following:



* define a remote service in a .proto file
* generate server and client stubs using the Protobuf compiler
* implement a server that provides this service
* implement a client that consumes this service


##### The contract

A *.proto* file is a *contract* between a service and a client, consisting of the following distinct sections:

*Syntax definition* to specify the version of the *proto* language being used.

*Package* to declare the namespace for the definitions to prevent naming conflicts.

*Imports* to allow the inclusion of definitions from other proto files, which is useful for their reusing.

*Options* to provide instructions to the protoc compiler on how to generate the code for various programming languages. <sup>Options can be applied at different scopes: file, message, field, enum, enum value, and service.</sup>

*Messages* to define data structures.

*Services* to define RPC services and the methods the service provides.

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
* *Bidirectional streaming*, where both sides send a sequence of messages using a read-write stream. The two streams operate independently, so clients and servers can read and write messages in any order. <sup>The order of messages in each stream is preserved.</sup>

>The *java_package* option specifies the package for the generated Java classes. If the *java_package* option is not explicitly specified in the *.proto* file, the package specified with the *package* keyword will be used by default. If code in another language is generated from this *.proto* file, the *java_package* option will have no effect.


##### Generating client and server code

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

For the EchoService service defined in the *.proto* file will be generated the EchoServiceGrpc class file contains the classes for providing and consuming this service. For providing this service will be generated the server sutb – the abstract inner EcoServiceImplBase class that you should extend and implement in the server to provide the remote service. For consuming this service wil be generated three different client stubs. The inner EcoServiceStub class you should use to make asynchronous remote calls using the StreamObserver interface (it supports all four communication patterns). The inner EcoServiceBlockingStub class you should use to make synchronous remote calls (it supports only unary and server-streaming calls). The inner EcoServiceFutureStub you should use to make asynchronous remote calls using the ListenableFuture interface (it supports only unary calls). Also the EchoServiceGrpc class contains static methods newStub, newBlockingStub, and newFutureStub, for creating instances of the various client stubs.

Also the EchoServiceGrpc contains another blocking stub – the inner EcoServiceBlockingV2Stub class and static method newBlockingV2Stub – if you want to use the checked StatusException exception instead of the rutime StatusRuntimeException exception. Use this blocking stub if you want to ensure that potencial gRPC errors will not be ignored. 
