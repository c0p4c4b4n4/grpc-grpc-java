##### What is gRPC

gRPC is a multi-lingual and cross-platform remote procedure call (RPC) framework initially developed by Google. gRPC is designed to provide high-performance inter-service interaction within and between data centers, as well as for resource-constrained mobile and IoT applications.

gRPC uses Protocol Buffers as binary serialization format and RPC interface description language, and HTTP/2 as transport layer protocol. Due to these features, gRPC can provide qualitative and quantitative characteristics of inter-service communication that are not available to REST (that most often means transferring textual JSONs over the HTTP/1.1 protocol).


##### The problem

When developing an effective RPC framework, developers had to address two primary challenges. First, developersneeded to ensure efficient cross-platform serialization. Solutions, based on textual formats (such as XML, JSON, or YAML) are typically an order of magnitude less efficient than binary formats. They require additional computational resources for serialization/deserialization and additional network resources for transmitting larger messages. (Solutions, based on binary formats often face significant challenges in ensuring portability across different languages ??and platforms).

Second, there was an absence of an efficient application-layer network protocol specifically designed for modern inter-service communication. The HTTP/1.1 protocol was originally designed to enable browsers to retrieve resources within the hypermedia networks. It was not designed to support high-speed, bidirectional, full-duplex communication. Various workarounds based on this protocol (short and long polling, streaming, webhooks) were inherently inefficient in their utilization of computational and network resources. (Solutions built on the TCP *transport layer* protocol were overly complex due to the low-level nature of the protocol and the lack of portability across different languages ??and platforms).


##### The solution

Since 2001, Google had been developing an internal RPC framework called Stubby. It was designed to connect almost all of internal services both within and across Google data centers. Stubby was a high-performance, cross-platform framework built on Protocol Buffers for serialization.

But only in 2015, with the appearance of the breakthrough HTTP/2 protocol, Google decided to leverage its features in a redesigned version of Stubby. References to Google's internal infrastructure were removed from the framework, and the project was redesigned to comply with public open source standards. The framework has also been adapted for use in mobile, IoT (Internet of Things), and cloud-native applications. This revamped version was released as gRPC (which is ironically deciphered as *gRPC Remote Procedure Calls*). 

Today, gRPC remains the primary mechanism for inter-service communication at Google. Also, Google offers gRPC interfaces alongside REST interfaces for many of its public services. This is because gRPC provides notable performance benefits and supports bidirectional streaming - a feature that is not achievable with traditional REST services.


##### gRPC foundations

The gRPC framework includes three main components:



* Protocol Buffers - a multi-language, cross-platform serialization framework
* IDL (Interface Definition Language) - an extension of Protocol Buffers for defining RPC interfaces
* HTTP/2 application-level protocol