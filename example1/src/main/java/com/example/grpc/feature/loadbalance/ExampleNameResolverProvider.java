package com.example.grpc.feature.loadbalance;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

import com.example.grpc.feature.loadbalance.Settings.*;

public class ExampleNameResolverProvider extends NameResolverProvider {
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        return new ExampleNameResolver(targetUri);
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    // gRPC choose the first NameResolverProvider that supports the target URI scheme.
    public String getDefaultScheme() {
        return Settings.exampleScheme;
    }
}
