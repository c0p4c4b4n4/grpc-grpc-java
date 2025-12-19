#### gRPC Remote Procedure Calls


##### What is gRPC

gRPC is a multi-lingual and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service interaction within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as a binary serialization format and RPC interface description language, and HTTP/2 as a transport layer protocol. Due to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available to REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


##### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, developers needed to ensure efficient cross-platform serialization. Solutions, based on textual formats (such as XML, JSON, or YAML), are typically an order of magnitude less efficient than binary formats. They require additional computational resources for serialization/deserialization and additional network resources for transmitting larger messages. Solutions, based on binary formats, often face significant challenges in ensuring portability across different languages ​​and platforms.

Second, there was an absence of an efficient application-layer network protocol specifically designed for modern inter-service communication. The HTTP/1.1 protocol was originally designed to enable browsers to retrieve resources within the hypermedia networks. It was not designed to support high-speed, bidirectional, full-duplex communication. Various workarounds based on this protocol (short and long polling, streaming, webhooks) were inherently inefficient in their utilization of computational and network resources. Solutions built on the TCP *transport layer* protocol were overly complex due to the low-level nature of the protocol and the lack of portability across different languages ​​and platforms.


##### The solution

Since 2001, Google had been developing an internal RPC framework called Stubby. It was designed to connect almost all of the internal services both within and across Google data centers. Stubby was a high-performance, cross-platform framework built on Protocol Buffers for serialization.

But only in 2015, with the appearance of the breakthrough HTTP/2 protocol, Google decided to leverage its features in a redesigned version of Stubby. References to Google's internal infrastructure were removed from the framework, and the project was redesigned to comply with public open source standards. The framework has also been adapted for use in mobile, IoT, and cloud-native applications. This revamped version was released as gRPC (which is recursively deciphered as *gRPC Remote Procedure Calls*).

Today, gRPC remains the primary mechanism for inter-service communication at Google. Also, Google offers gRPC interfaces alongside REST interfaces for many of its public services. This is because gRPC provides notable performance benefits and supports bidirectional streaming - a feature that is not achievable with traditional REST services.


##### gRPC foundations

The gRPC framework includes three main components:



* Protocol Buffers - a multi-language, cross-platform serialization framework
* IDL (Interface Definition Language) - an extension of Protocol Buffers for defining RPC interfaces
* HTTP/2 - an application-level protocol


###### Protocol Buffers

Protocol Buffers (Protobuf) is a serialization framework. It includes a compact binary serialization format *and* multi-language runtime libraries. This framework is optimized for exchanging short messages that fit entirely within device memory (usually less than a few megabytes).

Messages are described in a file with the *.proto* extension. This file, using the Protobuf compiler, generates domain objects in the selected programming language. Also, Protobuf includes runtime libraries for the conversion of these objects to and from the binary format.

Each message consists of fields with a *type*, *name*, and *identifier* (fields can also have optional *attributes*):


```
message Person {
  // scalar types
  double height = 1;
  float weight = 2;
  int32 social_credit = 3;
  bool is_sitizen = 4;
  string full_name = 5;
  bytes photo = 6; 

  // composite types
  enum Status { // enumeration
    UNKNOWN = 0;
    ON = 1;
    OFF = 3;
  }
  Status status = 7;

  message Address { // structure
    string country = 1;
    string city = 2;
    string street = 3;
  }
  Address primary_address = 8;

  map<string, string> attributes = 9; // map

  repeated string phone_numbers = 10; // array
}
```


*Types* contain *scalar* types - 32/64 bits integers, 32/64 bits floating-point numbers, boolean, strings (UTF-8 encoded or 7-bit ASCII text, and bytes, and *composite* types - enumerations, structures, maps, and arrays. Interestingly, the type system contains a few types for describing integer data. They allow developers to choose an efficient data type depending on whether the number is signed or unsigned and whether these values ​​are mostly small or uniformly distributed across the entire range.

*Names* are intended for developer understanding and are not included in the binary message.

*Identifiers* are used to uniquely identify field values in a binary message. These unique identifiers play a crucial role in ensuring backward and forward compatibility during the evolution of Protobuff messages. *Backward* compatibility means that newer code will read older messages. *Forward* compatibility means that older code will read newer messages: new fields that are not present in the old schema will be ignored, and old fields that are deleted in the new schema will have reasonable default values.

*Attributes* are additional metadata that can be added to messages. Previous versions of Protobuf had various attributes, but in version 3, only two remain: *optional* and *repeated*. The *optional* attribute is used to determine whether it was explicitly set or not, even if it's set to its default value. The *repeated* attribute is used to describe arrays.


###### Interface Definition Language

The interface description language is designed to describe the interface of RPC methods. Like message descriptions, interface descriptions are stored in a file with the *.proto* extension. This file, using the Protobuf compiler, converts pseudocode into client-server stubs in the selected programming language.

Depending on whether the method sends a single value or a stream and whether it returns a single value or a stream, there are 4 possible method types:



* unary: a simple call in which a client sends a single request and a server replies with a single response
* server-side streaming: a call in which a client sends a single request, but the server replies with multiple responses
* client-side streaming: a call in which a client sends multiple requests and a server replies with a single response. The server can opt to wait for the entire stream of client requests to cease before processing and responding
* bidirectional streaming: the client and server both send multiple calls back and forth concurrently, enabling real-time communication (full-duplex)


```
service BankService {
  // unary RPC
  rpc UnaryMethod(string) returns (string);

  // server streaming RPC
  rpc ServerStreamingMethod(string) returns (stream string);

  // client streaming RPC
  rpc ClientStreamingMethod(stream string) returns (string);

  // bidirectional streaming RPC
  rpc Bidirectional StreamingMethod(stream string) returns (stream string);
}
```


For backend developers who have long and unsuccessfully tried to implement simultaneous bidirectional inter-service communication with HTTP/1.1, it will be important to note that gRPC allows streaming from server to client, from client to server, and bidirectional simultaneous streaming.


###### HTTP/2

HTTP/2 is the next version of the HTTP transport protocol. Initially this protocol was developed to allow a client (usually a browser) to request resources (HTML documents, images, scripts) from a server in a hypermedia network. However, using this protocol to implement modern client-server systems (simultaneous bi-directional streaming) leads to complex and inefficient solutions. Even developing new features in HTTP/1.1 (persistent connections, pipelining, chunked transfer encoding) was not adequate.

HTTP/2 retains the semantics of the previous version of the protocol (methods, response codes, headers), but has significant implementation changes. HTTP/2 introduces several changes important for different environments (browsers, mobile, IoT), but only a few are important for gRPC.

The first important change is multiplexing, or the ability to send multiple concurrent streams on a single connection, over a single TCP connection. This solves the problem of the HTTP *head-of-line blocking*, where a slow response to a previous request slows down subsequent requests transmitted over the same TCP connection. This reduced latency and allowed the use of fewer TCP connections. Requests and responses are broken into frames (small data fragments), which are transmitted interleaved in the stream independently of each other. Multiplexing enables bidirectional, simultaneous data streaming between client and server.

>Multiplexing is the ability to send multiple concurrent streams on a single connection. gRPC uses channels to enable multiple streams over those multiple connections. Messages are sent as HTTP/2 data frames, each of which might contain multiple gRPC messages.

The second important change is the transition from a text-based format of headers and bodies of requests and responses to an encoded one. It involves using a binary format for recalculating request and response bodies and header compression (HPACK). If multiple requests and responses in a row share the same headers (which is common in inter-service interactions), the amount of bytes transferred for headers and their values ​​can be reduced by using *dictionaries* and *Huffman encoding*.

>HTTP/2 allows services to efficiently exchange information both in various simplex (unidirectional) modes and using a full-duplex (bidirectional) connection with simultaneous reception and transmission of messages.
