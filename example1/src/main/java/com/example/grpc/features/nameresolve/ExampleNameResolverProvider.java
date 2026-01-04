package com.example.grpc.features.nameresolve;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

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
    public String getDefaultScheme() {
        return Settings.SCHEME;
    }
}
