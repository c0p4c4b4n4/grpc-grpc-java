package com.example.grpc.nameresolve;

import com.google.common.collect.ImmutableMap;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ExampleNameResolver extends NameResolver {

    private final URI targetUri;
    private final Map<String, List<InetSocketAddress>> serviceNameToSocketAddresses;

    private Listener2 listener;

    ExampleNameResolver(URI targetUri) {
        this.targetUri = targetUri;
        this.serviceNameToSocketAddresses = ImmutableMap.<String, List<InetSocketAddress>>builder()
            .put(Settings.SERVICE_NAME,
                Arrays.stream(Settings.SERVER_PORTS)
                    .mapToObj(port -> new InetSocketAddress("localhost", port))
                    .collect(Collectors.toList())
            )
            .build();
    }

    @Override
    public String getServiceAuthority() {
        if (targetUri.getHost() != null) {
            return targetUri.getHost();
        }
        return "no host";
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void start(Listener2 listener) {
        this.listener = listener;
        this.resolve();
    }

    @Override
    public void refresh() {
        this.resolve();
    }

    private void resolve() {
        var addresses = serviceNameToSocketAddresses.get(targetUri.getPath().substring(1));
        try {
            var equivalentAddressGroups = addresses.stream()
                .map(this::toSocketAddress)
                .map(Arrays::asList)
                .map(this::toEquivalentAddressGroup)
                .collect(Collectors.toList());

            var resolutionResult = ResolutionResult.newBuilder()
                .setAddresses(equivalentAddressGroups)
                .build();
            this.listener.onResult(resolutionResult);
        } catch (Exception e) {
            this.listener.onError(Status.UNAVAILABLE.withDescription("Unable to resolve host").withCause(e));
        }
    }

    private SocketAddress toSocketAddress(InetSocketAddress address) {
        return new InetSocketAddress(address.getHostName(), address.getPort());
    }

    private EquivalentAddressGroup toEquivalentAddressGroup(List<SocketAddress> addresses) {
        return new EquivalentAddressGroup(addresses);
    }
}
