#### gRPC Remote Procedure Calls


##### What is gRPC

gRPC is a multi-lingual and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service interaction within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as a binary serialization format and RPC interface description language, and HTTP/2 as a transport layer protocol. Due to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available to REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


##### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, developers needed to ensure efficient cross-platform serialization. Solutions, based on textual formats (such as XML, JSON, or YAML), are typically an order of magnitude less efficient than binary formats. They require additional computational resources for serialization/deserialization and additional network resources for transmitting larger messages. (Solutions, based on binary formats, often face significant challenges in ensuring portability across different languages ​​and platforms).

Second, there was an absence of an efficient application-layer network protocol specifically designed for modern inter-service communication. The HTTP/1.1 protocol was originally designed to enable browsers to retrieve resources within the hypermedia networks. It was not designed to support high-speed, bidirectional, full-duplex communication. Various workarounds based on this protocol (short and long polling, streaming, webhooks) were inherently inefficient in their utilization of computational and network resources. (Solutions built on the TCP *transport layer* protocol were overly complex due to the low-level nature of the protocol and the lack of portability across different languages ​​and platforms).


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


*Types* contain scalar types (32/64 bits integers, 32/64 bits floating-point numbers, boolean, strings (UTF-8 encoded or 7-bit ASCII text), and bytes), and composite types (enumerations, structures, maps, and arrays). Interestingly, the type system contains a few types for describing integer data. They allow developers to choose an efficient data type depending on whether the number is signed or unsigned and whether these values ​​are mostly small or uniformly distributed across the entire range.

*Names* are intended for developer understanding and are not included in the binary message.

*Identifiers* are used to uniquely identify field values in a binary message. These unique identifiers play a crucial role in ensuring backward and forward compatibility during the evolution of Protobuff messages. *Backward* compatibility means that newer code will read older messages. *Forward* compatibility means that older code will read newer messages: new fields that are not present in the old schema will be ignored, and old fields that are deleted in the new schema will have reasonable default values.

*Attributes* are additional metadata that can be added to messages. Previous versions of Protobuf had various *attributes*, but in version 3, only two remain: *optional* and *repeated*. The *optional* attribute is used to determine whether it was explicitly set or not, even if it's set to its default value. The *repeated* attribute is used to describe arrays.
