package com.example.grpc;

import com.example.grpc.features.healthservice.UnaryBlockingClient;

import java.util.logging.Logger;

public class Loggable {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());
}
