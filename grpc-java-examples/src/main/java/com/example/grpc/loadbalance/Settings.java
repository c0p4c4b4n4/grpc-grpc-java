package com.example.grpc.loadbalance;

interface Settings {

    String SCHEME = "example";
    String SERVICE_NAME = "example.grpc.loadbalance";
    int[] SERVER_PORTS = {50051, 50052, 50053};
}
